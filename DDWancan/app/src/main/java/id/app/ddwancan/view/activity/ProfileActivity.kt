package id.app.ddwancan.view.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import id.app.ddwancan.data.local.SettingsPreference
import id.app.ddwancan.ui.theme.DDwancanTheme
import id.app.ddwancan.view.screen.profile.ProfileScreen

import id.app.ddwancan.view.activity.EditProfileActivity
import id.app.ddwancan.view.activity.LoginActivity

class ProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val context = this@ProfileActivity
            val settings = remember { SettingsPreference(context) }
            val isDarkMode by settings.isDarkMode.collectAsState(initial = false)
            
            DDwancanTheme(darkTheme = isDarkMode) {
                ProfileScreen(
                    onEditClick = {
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
