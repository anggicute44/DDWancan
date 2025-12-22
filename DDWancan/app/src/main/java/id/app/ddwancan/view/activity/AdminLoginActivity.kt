package id.app.ddwancan.view.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import id.app.ddwancan.ui.theme.DDwancanTheme
import id.app.ddwancan.view.screen.auth.AdminLoginScreen

class AdminLoginActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DDwancanTheme {
                AdminLoginScreen(
                    onLoginClick = { username, password ->
                        performAdminLogin(username, password)
                    },
                    onBackToUserLogin = {
                        // Kembali ke halaman Login User
                        finish()
                    }
                )
            }
        }
    }

    private fun performAdminLogin(username: String, pass: String) {
        if (username.isBlank() || pass.isBlank()) {
            Toast.makeText(this, "Username & Password wajib diisi", Toast.LENGTH_SHORT).show()
            return
        }

        // TODO: Ganti logika ini dengan cek ke Database / API Backend Anda
        // Ini hanya hardcoded untuk contoh
        if (username == "admin" && pass == "admin123") {
            Toast.makeText(this, "Login Admin Berhasil", Toast.LENGTH_SHORT).show()
            goToAdminDashboard()
        } else {
            Toast.makeText(this, "Username atau Password salah!", Toast.LENGTH_SHORT).show()
        }
    }

    // Di dalam AdminLoginActivity.kt

    private fun goToAdminDashboard() {
        val intent = Intent(this, AdminDashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}