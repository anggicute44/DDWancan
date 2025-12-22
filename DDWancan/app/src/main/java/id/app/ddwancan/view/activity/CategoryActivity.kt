package id.app.ddwancan.view.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import id.app.ddwancan.ui.theme.DDwancanTheme
import id.app.ddwancan.view.screen.category.CategoryScreen

class CategoryActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🔹 Ambil kategori dari HomeScreen
        val category = intent.getStringExtra("CATEGORY") ?: "ALL"

        setContent {
            DDwancanTheme {
                CategoryScreen(
                    category = category,
                    onBack = { finish() }
                )
            }
        }
    }
}
