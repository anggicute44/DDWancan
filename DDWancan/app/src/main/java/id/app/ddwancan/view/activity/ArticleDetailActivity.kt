package id.app.ddwancan.view.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import id.app.ddwancan.data.local.SettingsPreference
import id.app.ddwancan.ui.theme.DDwancanTheme
import id.app.ddwancan.view.screen.detail.ArticleDetailScreen

class ArticleDetailActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sourceId = intent.getStringExtra("SOURCE_ID") ?: "unknown"
        val title = intent.getStringExtra("TITLE") ?: ""
        val content = intent.getStringExtra("CONTENT") ?: "Konten tidak tersedia"
        val imageUrl = intent.getStringExtra("IMAGE")
        val articleUrl = intent.getStringExtra("URL") ?: ""
        val author = intent.getStringExtra("AUTHOR")
        val publishedAt = intent.getStringExtra("PUBLISHED_AT")

        setContent {
            val context = this@ArticleDetailActivity
            val settings = remember { SettingsPreference(context) }
            val isDarkMode by settings.isDarkMode.collectAsState(initial = false)
            
            DDwancanTheme(darkTheme = isDarkMode) {
                ArticleDetailScreen(
                    sourceId = sourceId,
                    title = title,
                    content = content,
                    imageUrl = imageUrl,
                    articleUrl = articleUrl,
                    author = author,
                    publishedAt = publishedAt,
                    onBack = { finish() }
                )
            }
        }
    }
}
