package id.app.ddwancan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import id.app.ddwancan.data.local.NewsEntity
import id.app.ddwancan.data.local.SettingsPreference
import id.app.ddwancan.ui.theme.DDwancanTheme
import id.app.ddwancan.view.screen.home.HomeScreen
import id.app.ddwancan.viewmodel.MainViewModel
import id.app.ddwancan.viewmodel.ViewModelFactory

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels {
        ViewModelFactory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ Tempat yang BENAR untuk refresh data
        viewModel.refreshData()

        setContent {
            val context = LocalContext.current
            val settings = remember { SettingsPreference(context) }

            val isDarkMode by settings.isDarkMode.collectAsState(initial = false)

            DDwancanTheme(darkTheme = isDarkMode) {
                HomeScreen()
            }
        }

    }
}

/* =========================================================
   HOME / NEWS SCREEN
========================================================= */
@Composable
fun NewsScreen(
    newsList: List<NewsEntity>,
    modifier: Modifier = Modifier
) {
    if (newsList.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Belum ada berita tersimpan.")
        }
    } else {
        LazyColumn(
            modifier = modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(newsList) { news ->
                NewsItem(news)
            }
        }
    }
}

/* =========================================================
   NEWS ITEM CARD
========================================================= */
@Composable
fun NewsItem(news: NewsEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = news.title,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = news.publishedAt ?: "-",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = news.description ?: "Tidak ada deskripsi",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
