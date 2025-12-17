package id.app.ddwancan.view.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import id.app.ddwancan.view.screen.detail.ArticleDetailScreen

class ArticleDetailActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Ambil data dari Intent
        val articleId = intent.getStringExtra("ARTICLE_ID") ?: ""
        val title = intent.getStringExtra("TITLE") ?: ""
        val content = intent.getStringExtra("CONTENT") ?: "Konten tidak tersedia"
        val imageUrl = intent.getStringExtra("IMAGE")
        val articleUrl = intent.getStringExtra("URL") ?: ""

        setContent {
            ArticleDetailScreen(
                articleId = articleId,
                title = title,
                content = content,
                imageUrl = imageUrl,
                articleUrl = articleUrl, // 🔥 Kirim URL ke Composable
                onBack = { finish() } // 🔥 Tambahkan fungsi onBack untuk menutup Activity
            )
        }
    }
}
