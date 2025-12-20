package id.app.ddwancan.view.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import id.app.ddwancan.R
import id.app.ddwancan.data.utils.UserSession
import id.app.ddwancan.ui.theme.DDwancanTheme
import id.app.ddwancan.view.screen.auth.LoginScreen

class LoginActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleClient: GoogleSignInClient

    private val googleLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.result
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            auth.signInWithCredential(credential)
                .addOnSuccessListener { goToHome() }
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

        setContent {
            DDwancanTheme {
                LoginScreen(
                    onEmailLogin = { email, password ->
                        loginWithEmail(email, password)
                    },
                    onGoogleLogin = {
                        signInWithGoogle()
                    },
                    onAdminLoginClick = {
                        // Pindah ke halaman Login Admin
                        val intent = Intent(this, AdminLoginActivity::class.java)
                        startActivity(intent)
                    },
                    // 👇 UPDATE BAGIAN INI AGAR MEMBUKA REGISTER ACTIVITY
                    onSignUpClick = {
                        val intent = Intent(this, RegisterActivity::class.java)
                        startActivity(intent)
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
            .addOnSuccessListener { goToHome() }
            .addOnFailureListener {
                Toast.makeText(this, it.message ?: "Login Gagal", Toast.LENGTH_SHORT).show()
            }
    }

    private fun signInWithGoogle() {
        googleLauncher.launch(googleClient.signInIntent)
    }

    private fun goToHome() {
        val currentUser = auth.currentUser
        UserSession.userId = currentUser?.uid
        Log.d("LoginActivity", "User ID: ${UserSession.userId}")

        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}