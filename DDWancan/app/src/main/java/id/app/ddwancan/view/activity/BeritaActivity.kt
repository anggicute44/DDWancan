package id.app.ddwancan.view.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import id.app.ddwancan.data.local.SettingsPreference
import id.app.ddwancan.data.model.NewsViewModel

import id.app.ddwancan.ui.theme.DDwancanTheme
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

        // Panggil fungsi yang benar untuk refresh berita
        viewModel.refreshFromRemote()

        setContent {
            val context = this@BeritaActivity
            val settings = remember { SettingsPreference(context) }
            val isDarkMode by settings.isDarkMode.collectAsState(initial = false)
            
            DDwancanTheme(darkTheme = isDarkMode) {
                NewsListScreen(viewModel = viewModel)
            }
        }
    }

    companion object {
        const val EXTRA_CATEGORY = "EXTRA_CATEGORY"
    }
}
