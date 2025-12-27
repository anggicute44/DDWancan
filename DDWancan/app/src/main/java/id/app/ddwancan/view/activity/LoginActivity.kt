package id.app.ddwancan.view.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import id.app.ddwancan.R
import id.app.ddwancan.data.local.SettingsPreference
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

    private var showEnableFingerprintDialog by mutableStateOf(false)
    private var onDialogConfirm: () -> Unit = {}

    private val googleLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        try {
            val account = GoogleSignIn.getSignedInAccountFromIntent(result.data).getResult(ApiException::class.java)!!
            firebaseAuthWithGoogle(account)
        } catch (e: ApiException) {
            Toast.makeText(this, "Login Google gagal atau dibatalkan.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
        googleClient = GoogleSignIn.getClient(this, gso)

        setupBiometricPrompt()

        setContent {
            val context = this@LoginActivity
            val settings = remember { SettingsPreference(context) }
            val isDarkMode by settings.isDarkMode.collectAsState(initial = false)

            DDwancanTheme(darkTheme = isDarkMode) {
                LoginScreen(
                    onEmailLogin = ::loginWithEmail,
                    onGoogleLogin = ::signInWithGoogle,
                    onFingerprintLogin = ::signInWithFingerprint,
                    onAdminLoginClick = { startActivity(Intent(this, AdminLoginActivity::class.java)) },
                    onSignUpClick = { startActivity(Intent(this, RegisterActivity::class.java)) }
                )

                if (showEnableFingerprintDialog) {
                    EnableFingerprintDialog(
                        onConfirm = onDialogConfirm,
                        onDismiss = { showEnableFingerprintDialog = false; goToHome() } // Jika batal, langsung ke home
                    )
                }
            }
        }
    }

    private fun loginWithEmail(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            Toast.makeText(this, "Email & Password wajib diisi", Toast.LENGTH_SHORT).show()
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { syncUserToFirestoreAndProceed(email, password) }
            .addOnFailureListener { e -> Toast.makeText(this, e.message ?: "Login Gagal", Toast.LENGTH_SHORT).show() }
    }

    private fun signInWithGoogle() {
        googleClient.signOut().addOnCompleteListener { googleLauncher.launch(googleClient.signInIntent) }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        auth.signInWithCredential(GoogleAuthProvider.getCredential(account.idToken!!, null))
            .addOnSuccessListener { syncUserToFirestoreAndProceed(null, null) }
            .addOnFailureListener { e -> Toast.makeText(this, "Otentikasi Firebase gagal: ${e.message}", Toast.LENGTH_SHORT).show() }
    }

    private fun signInWithFingerprint() {
        val prefs = getSharedPreferences("DDWancanPrefs", Context.MODE_PRIVATE)
        if (prefs.contains("fingerprint_email") && prefs.contains("fingerprint_password")) {
            biometricPrompt.authenticate(promptInfo)
        } else {
            Toast.makeText(this, "Fitur sidik jari belum diaktifkan di perangkat ini.", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupBiometricPrompt() {
        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                val prefs = getSharedPreferences("DDWancanPrefs", Context.MODE_PRIVATE)
                val email = prefs.getString("fingerprint_email", null)
                val password = prefs.getString("fingerprint_password", null)
                if (email != null && password != null) {
                    Toast.makeText(this@LoginActivity, "Sidik jari dikenali, mencoba login...", Toast.LENGTH_SHORT).show()
                    loginWithEmail(email, password)
                } else {
                    Toast.makeText(this@LoginActivity, "Gagal mendapatkan kredensial tersimpan.", Toast.LENGTH_SHORT).show()
                }
            }
        })
        promptInfo = BiometricPrompt.PromptInfo.Builder().setTitle("Login Sidik Jari")
            .setSubtitle("Sentuh sensor sidik jari untuk login").setNegativeButtonText("Batal").build()
    }

    private fun promptToEnableFingerprint(email: String, password: String, onComplete: () -> Unit) {
        val biometricManager = BiometricManager.from(this)
        if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS) {
            onDialogConfirm = { registerFingerprint(email, password, onComplete) }
            showEnableFingerprintDialog = true
        } else {
            onComplete()
        }
    }

    @Composable
    private fun EnableFingerprintDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Aktivasi Login Cepat") },
            text = { Text("Aktifkan login menggunakan sidik jari untuk masuk lebih cepat di lain waktu?") },
            confirmButton = { TextButton(onClick = { showEnableFingerprintDialog = false; onConfirm() }) { Text("Aktifkan") } },
            dismissButton = { TextButton(onClick = onDismiss) { Text("Nanti Saja") } }
        )
    }

    private fun registerFingerprint(email: String, password: String, onComplete: () -> Unit) {
        val registrationPromptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Daftarkan Sidik Jari")
            .setSubtitle("Verifikasi sidik jari Anda untuk mengaktifkan fitur ini")
            .setNegativeButtonText("Batal").build()

        BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                val prefs = getSharedPreferences("DDWancanPrefs", Context.MODE_PRIVATE)
                // PERINGATAN: Menyimpan kredensial dalam teks biasa tidak aman untuk produksi.
                prefs.edit().putString("fingerprint_email", email).putString("fingerprint_password", password).apply()
                Toast.makeText(this@LoginActivity, "Login sidik jari telah diaktifkan!", Toast.LENGTH_SHORT).show()
                onComplete()
            }
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                Toast.makeText(this@LoginActivity, "Pendaftaran sidik jari dibatalkan.", Toast.LENGTH_SHORT).show()
                onComplete()
            }
        }).authenticate(registrationPromptInfo)
    }

    private fun goToHome() {
        startActivity(Intent(this, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
    }

    // PERBAIKAN: Menggunakan SharedPreferences untuk melacak status penawaran sidik jari
    private fun syncUserToFirestoreAndProceed(email: String?, password: String?) {
        val firebaseUser = auth.currentUser ?: return goToHome()
        UserSession.userId = firebaseUser.uid

        val prefs = getSharedPreferences("DDWancanPrefs", Context.MODE_PRIVATE)

        // Cek keamanan untuk menghapus kredensial lama jika user berbeda
        val registeredEmail = prefs.getString("fingerprint_email", null)
        if (registeredEmail != null && registeredEmail != firebaseUser.email) {
            prefs.edit().remove("fingerprint_email").remove("fingerprint_password").apply()
            Toast.makeText(this, "Kredensial sidik jari lama dihapus untuk keamanan.", Toast.LENGTH_LONG).show()
        }

        val userRef = db.collection("User").document(firebaseUser.uid)

        userRef.get().addOnSuccessListener { doc ->
            val onSyncComplete = {
                val promptFlag = "fingerprint_prompt_shown_for_${firebaseUser.uid}"
                val hasBeenPrompted = prefs.getBoolean(promptFlag, false)

                val onPromptFlowFinished = {
                    // Tandai bahwa prompt sudah ditampilkan untuk user ini
                    prefs.edit().putBoolean(promptFlag, true).apply()
                    goToHome()
                }

                if (email != null && password != null && !hasBeenPrompted) {
                    // Ini adalah pengguna email/pass yang belum pernah ditawari.
                    promptToEnableFingerprint(email, password, onPromptFlowFinished)
                } else {
                    // Pengguna Google, atau pengguna email yang sudah pernah ditawari.
                    goToHome()
                }
            }

            if (!doc.exists()) {
                // Jika dokumen user belum ada, buat dulu.
                val data = hashMapOf(
                    "uid" to firebaseUser.uid,
                    "name" to (firebaseUser.displayName ?: firebaseUser.email!!.substringBefore('@')),
                    "email" to firebaseUser.email, "role" to "user", "avatar" to 0,
                    "createdAt" to FieldValue.serverTimestamp()
                )
                userRef.set(data).addOnCompleteListener { onSyncComplete() }
            } else {
                // Jika dokumen sudah ada, langsung jalankan logika prompt.
                onSyncComplete()
            }
        }.addOnFailureListener { goToHome() }
    }
}
