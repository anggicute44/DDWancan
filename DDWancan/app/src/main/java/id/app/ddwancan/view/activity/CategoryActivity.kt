package id.app.ddwancan.view.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import id.app.ddwancan.data.local.SettingsPreference
import id.app.ddwancan.ui.theme.DDwancanTheme
import id.app.ddwancan.view.screen.category.CategoryScreen

class CategoryActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val category = intent.getStringExtra("CATEGORY") ?: "ALL"

        setContent {
            val context = this@CategoryActivity
            val settings = remember { SettingsPreference(context) }
            val isDarkMode by settings.isDarkMode.collectAsState(initial = false)
            
            DDwancanTheme(darkTheme = isDarkMode) {
                CategoryScreen(
                    category = category,
                    onBack = { finish() }
                )
            }
        }
    }
}
