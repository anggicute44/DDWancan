package id.app.ddwancan.view.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels // Perlu dependency activity-ktx atau lifecycle-viewmodel-compose
import androidx.compose.material3.MaterialTheme
import id.app.ddwancan.data.model.NewsViewModel
import id.app.ddwancan.ui.theme.DDwancanTheme // Sesuaikan dengan nama project Anda
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.app.ddwancan.view.screen.berita.NewsListScreen
import kotlinx.coroutines.launch

class BeritaActivity : ComponentActivity() {

    private val viewModel by viewModels<NewsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val category = intent.getStringExtra(EXTRA_CATEGORY)

        viewModel.fetchNews(category)

        setContent {
            DDwancanTheme {
                NewsListScreen(viewModel = viewModel)
            }
        }
    }

    companion object {
        const val EXTRA_CATEGORY = "EXTRA_CATEGORY"
    }
}
