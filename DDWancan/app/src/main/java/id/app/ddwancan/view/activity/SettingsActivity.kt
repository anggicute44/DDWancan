package id.app.ddwancan.view.activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import id.app.ddwancan.data.local.SettingsPreference
import id.app.ddwancan.ui.theme.DDwancanTheme
import id.app.ddwancan.view.screen.settings.SettingsScreen
import kotlinx.coroutines.launch

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val context = this@SettingsActivity
            val settings = remember { SettingsPreference(context) }
            val scope = rememberCoroutineScope()
            
            val isDarkMode by settings.isDarkMode.collectAsState(initial = false)
            val isEnglish by settings.isEnglish.collectAsState(initial = false)

            DDwancanTheme(darkTheme = isDarkMode) {
                SettingsScreen(
                    onBack = { finish() },
                    isDarkMode = isDarkMode,
                    isEnglish = isEnglish,
                    onDarkModeChange = {
                        scope.launch {
                            settings.saveDarkMode(it)
                        }
                        Toast.makeText(this@SettingsActivity, "Mode Gelap: $it", Toast.LENGTH_SHORT).show()
                    },
                    onLanguageChange = {
                        scope.launch {
                            settings.saveLanguage(it)
                        }
                        Toast.makeText(this@SettingsActivity, "Bahasa: $it", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}
