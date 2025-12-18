package id.app.ddwancan.view.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import id.app.ddwancan.view.screen.detail.ArticleDetailScreen

class ArticleDetailActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Ambil data dari Intent, termasuk ID sumber berita
        val sourceId = intent.getStringExtra("SOURCE_ID") ?: "unknown"
        val title = intent.getStringExtra("TITLE") ?: ""
        val content = intent.getStringExtra("CONTENT") ?: "Konten tidak tersedia"
        val imageUrl = intent.getStringExtra("IMAGE")
        val articleUrl = intent.getStringExtra("URL") ?: ""

        setContent {
            ArticleDetailScreen(
                sourceId = sourceId, // Teruskan sourceId ke Composable
                title = title,
                content = content,
                imageUrl = imageUrl,
                articleUrl = articleUrl,
                onBack = { finish() }
            )
        }
    }
}
