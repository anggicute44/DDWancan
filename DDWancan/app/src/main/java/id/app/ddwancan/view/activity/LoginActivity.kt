package id.app.ddwancan.view.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import id.app.ddwancan.R
import id.app.ddwancan.data.utils.UserSession
import id.app.ddwancan.ui.theme.DDwancanTheme
import id.app.ddwancan.view.screen.auth.LoginScreen
import java.util.concurrent.Executor

class LoginActivity : FragmentActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleClient: GoogleSignInClient
    private val db = FirebaseFirestore.getInstance()

    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    private val googleLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.result
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            auth.signInWithCredential(credential)
                .addOnSuccessListener {
                    // PERBAIKAN: Langsung ke Home setelah sinkronisasi
                    syncUserToFirestoreAndProceed {
                        goToHome()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Login Google gagal", Toast.LENGTH_SHORT).show()
                }
        } catch (e: Exception) {
            Toast.makeText(this, "Login dibatalkan", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleClient = GoogleSignIn.getClient(this, gso)

        setupBiometricPrompt()

        setContent {
            DDwancanTheme(darkTheme = isSystemInDarkTheme()) {
                LoginScreen(
                    onEmailLogin = { email, password ->
                        loginWithEmail(email, password)
                    },
                    onGoogleLogin = {
                        signInWithGoogle()
                    },
                    onFingerprintLogin = {
                        signInWithFingerprint()
                    },
                    onAdminLoginClick = {
                        startActivity(Intent(this, AdminLoginActivity::class.java))
                    },
                    onSignUpClick = {
                        startActivity(Intent(this, RegisterActivity::class.java))
                    }
                )
            }
        }
    }

    private fun loginWithEmail(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            Toast.makeText(this, "Email & Password wajib diisi", Toast.LENGTH_SHORT).show()
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                // PERBAIKAN: Langsung ke Home setelah sinkronisasi
                syncUserToFirestoreAndProceed {
                    goToHome()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, it.message ?: "Login Gagal", Toast.LENGTH_SHORT).show()
            }
    }

    private fun signInWithGoogle() {
        googleLauncher.launch(googleClient.signInIntent)
    }

    private fun signInWithFingerprint() {
        if (auth.currentUser != null) {
            biometricPrompt.authenticate(promptInfo)
        } else {
            Toast.makeText(
                this,
                "Silakan login dengan Email/Google dulu untuk mengaktifkan fitur ini",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun setupBiometricPrompt() {
        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Toast.makeText(this@LoginActivity, "Login sidik jari berhasil!", Toast.LENGTH_SHORT).show()
                    // PERBAIKAN: Langsung ke Home, bukan ke FingerprintActivity
                    goToHome()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    if (errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                        Toast.makeText(this@LoginActivity, "Error autentikasi: $errString", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(this@LoginActivity, "Sidik jari tidak dikenali.", Toast.LENGTH_SHORT).show()
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Login Sidik Jari")
            .setSubtitle("Sentuh sensor sidik jari untuk login")
            .setNegativeButtonText("Batal")
            .build()
    }

    // PERBAIKAN: Fungsi baru untuk navigasi ke HomeActivity
    private fun goToHome() {
        val currentUser = auth.currentUser
        UserSession.userId = currentUser?.uid

        Log.d("LoginActivity", "Login Success, User ID: ${UserSession.userId}. Navigating to Home.")

        val intent = Intent(this, HomeActivity::class.java)
        // Flag ini penting untuk membuat HomeActivity sebagai root baru dan menghapus riwayat
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun syncUserToFirestoreAndProceed(onDone: () -> Unit) {
        val firebaseUser = auth.currentUser ?: return onDone()

        val userRef = db.collection("User").document(firebaseUser.uid)

        userRef.get().addOnSuccessListener { doc ->
            if (!doc.exists()) {
                val data = hashMapOf(
                    "uid" to firebaseUser.uid,
                    "name" to (firebaseUser.displayName ?: firebaseUser.email!!.substringBefore('@')),
                    "email" to firebaseUser.email,
                    "role" to "user",
                    "avatar" to 0,
                    "createdAt" to FieldValue.serverTimestamp()
                )
                userRef.set(data).addOnCompleteListener { onDone() }
            } else {
                onDone()
            }
        }.addOnFailureListener {
            // Tetap lanjutkan meskipun gagal sinkronisasi, agar user tidak stuck
            onDone()
        }
    }
}