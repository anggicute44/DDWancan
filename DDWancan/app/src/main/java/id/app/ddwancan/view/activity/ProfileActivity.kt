package id.app.ddwancan.view.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import id.app.ddwancan.ui.theme.DDwancanTheme
import id.app.ddwancan.view.screen.profile.ProfileScreen

// PERBAIKAN: Menambahkan import yang hilang
import id.app.ddwancan.view.activity.EditProfileActivity
import id.app.ddwancan.view.activity.LoginActivity

class ProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DDwancanTheme {
                ProfileScreen(
                    onEditClick = {
                        // Navigasi ke EditProfileActivity
                        val intent = Intent(this@ProfileActivity, EditProfileActivity::class.java)
                        startActivity(intent)
                    },
                    // 👇 Callback untuk Logout
                    onNavigateToLogin = {
                        val intent = Intent(this@ProfileActivity, LoginActivity::class.java)
                        // Flag ini penting: Menghapus history agar user tidak bisa tekan 'Back' ke profil
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                )
            }
        }
    }
}
