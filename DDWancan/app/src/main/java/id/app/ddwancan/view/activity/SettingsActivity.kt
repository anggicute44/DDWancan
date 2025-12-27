package id.app.ddwancan.view.activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import id.app.ddwancan.ui.theme.DDwancanTheme
import id.app.ddwancan.view.screen.settings.SettingsScreen

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            // PERBAIKAN: Panggil isSystemInDarkTheme() di sini, dalam konteks @Composable yang sah.
            val systemIsDark = isSystemInDarkTheme()

            // Gunakan nilainya untuk menginisialisasi state. Blok remember sekarang tidak
            // lagi memanggil fungsi @Composable, sehingga valid.
            var isDarkMode by remember { mutableStateOf(systemIsDark) }
            var isEnglish by remember { mutableStateOf(false) }

            DDwancanTheme(darkTheme = isDarkMode) {
                SettingsScreen(
                    onBack = { finish() },
                    isDarkMode = isDarkMode,
                    isEnglish = isEnglish,
                    onDarkModeChange = {
                        isDarkMode = it
                        Toast.makeText(this, "Mode Gelap: $it", Toast.LENGTH_SHORT).show()
                    },
                    onLanguageChange = {
                        isEnglish = it
                        Toast.makeText(this, "Bahasa Inggris: $it", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}
