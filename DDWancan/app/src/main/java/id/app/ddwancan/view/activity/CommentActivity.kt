package id.app.ddwancan.view.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels

// import id.app.ddwancan.ui.theme.DDwancanTheme // Theme might be named differently or default MaterialTheme used
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import id.app.ddwancan.view.screen.comment.CommentScreen

class CommentActivity : ComponentActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Mengambil data menggunakan key yang sama dengan yang dikirim dari ArticleDetailScreen
        val articleId = intent.getStringExtra("ARTICLE_ID") ?: ""
        val title = intent.getStringExtra("TITLE") ?: "Comments"

        setContent {
            // Gunakan MaterialTheme default jika DDwancanTheme bermasalah
            MaterialTheme {
                CommentScreen(
                    
                    articleId = articleId,
                    title = title,
                    onBack = { finish() }
                )
            }
        }
    }
}
