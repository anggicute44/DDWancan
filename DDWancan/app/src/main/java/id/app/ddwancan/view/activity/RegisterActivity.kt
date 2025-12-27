package id.app.ddwancan.view.activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import id.app.ddwancan.data.local.SettingsPreference
import id.app.ddwancan.ui.theme.DDwancanTheme
import id.app.ddwancan.view.screen.auth.RegisterScreen

class RegisterActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        setContent {
            val context = this@RegisterActivity
            val settings = remember { SettingsPreference(context) }
            val isDarkMode by settings.isDarkMode.collectAsState(initial = false)
            
            DDwancanTheme(darkTheme = isDarkMode) {
                RegisterScreen(
                    onRegisterClick = { name, email, password ->
                        performRegister(name, email, password)
                    },
                    onBackToLoginClick = {
                        finish()
                    }
                )
            }
        }
    }

    private fun performRegister(name: String, email: String, pass: String) {
        if (name.isBlank() || email.isBlank() || pass.isBlank()) {
            Toast.makeText(this, "Semua kolom wajib diisi", Toast.LENGTH_SHORT).show()
            return
        }

        // 1. Buat Akun di Firebase Auth
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnSuccessListener { authResult ->
                val userId = authResult.user?.uid
                if (userId != null) {
                    saveUserToFirestore(userId, name, email)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal Daftar: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveUserToFirestore(uid: String, name: String, email: String) {
        // Data yang akan disimpan
        val userData = hashMapOf(
            "uid" to uid,
            "name" to name,
            "email" to email,
            "role" to "user", // Default role
            "createdAt" to com.google.firebase.Timestamp.now()
        )

        // 2. Simpan ke Firestore db.collection("User")
        db.collection("User") // Sesuai request Anda
            .document(uid)
            .set(userData)
            .addOnSuccessListener {
                Toast.makeText(this, "Registrasi Berhasil!", Toast.LENGTH_SHORT).show()
                finish() // Tutup halaman register, user kembali ke login atau bisa langsung login
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal simpan data: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}