package id.app.ddwancan.view.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import id.app.ddwancan.ui.theme.DDwancanTheme
import id.app.ddwancan.view.screen.admin.AdminDashboardScreen

class AdminDashboardActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DDwancanTheme {
                AdminDashboardScreen(
                    onMenuClick = { menuId ->
                        handleMenuNavigation(menuId)
                    },
                    onLogoutClick = {
                        performLogout()
                    }
                )
            }
        }
    }

    private fun handleMenuNavigation(menuId: String) {
        when (menuId) {
            "manage_users" -> {
                startActivity(Intent(this, ManageUsersActivity::class.java))
            }
            "manage_comments" -> {
                startActivity(Intent(this, ManageCommentsActivity::class.java))
            }
                "update_news" -> {
                    startActivity(Intent(this, AdminUpdateNewsActivity::class.java))
                }
        }
    }

    private fun performLogout() {
        // 1. Hapus session admin (jika Anda menyimpannya di SharedPreferences/DataStore)
        // UserSession.clearAdminSession() // Opsional

        // 2. Tampilkan pesan
        Toast.makeText(this, "Logout Admin Berhasil", Toast.LENGTH_SHORT).show()

        // 3. Kembali ke Halaman Login Utama (LoginActivity)
        val intent = Intent(this, LoginActivity::class.java)
        // Clear Task flags agar user tidak bisa back ke dashboard setelah logout
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }


}