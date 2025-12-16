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

    // Inisialisasi ViewModel
    private val viewModel by viewModels<NewsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Panggil Theme bawaan project (biasanya dibuat otomatis)
            // Ganti 'YourAppTheme' dengan nama theme project Anda
            MaterialTheme {
                // Panggil Screen Composable yang kita buat
                NewsListScreen(viewModel = viewModel)
            }
        }
    }
}