package id.app.ddwancan.view.activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import id.app.ddwancan.data.local.SettingsPreference
import id.app.ddwancan.ui.theme.DDwancanTheme
import id.app.ddwancan.view.screen.profile.EditProfileScreen
import kotlinx.coroutines.launch

class EditProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = this@EditProfileActivity
            val settings = remember { SettingsPreference(context) }
            val scope = rememberCoroutineScope()
            val isDarkMode by settings.isDarkMode.collectAsState(initial = false)
            
            DDwancanTheme(darkTheme = isDarkMode) {
                EditProfileScreen(
                    onBackClick = { 
                        finish()
                    },
                    onDarkModeChange = {
                        scope.launch {
                            settings.saveDarkMode(it)
                        }
                        Toast.makeText(this@EditProfileActivity, "Mode Gelap: $it", Toast.LENGTH_SHORT).show()
                    },
                    onLanguageChange = {
                        scope.launch {
                            settings.saveLanguage(it)
                        }
                        Toast.makeText(this@EditProfileActivity, "Bahasa diubah: $it", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}
