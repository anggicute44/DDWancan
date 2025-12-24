package id.app.ddwancan

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue // Import ini penting untuk observeAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import id.app.ddwancan.data.local.NewsEntity
import id.app.ddwancan.ui.theme.DDwancanTheme
import id.app.ddwancan.viewmodel.MainViewModel
import id.app.ddwancan.viewmodel.ViewModelFactory

class MainActivity : ComponentActivity() {

    // 1. Inisialisasi ViewModel menggunakan Factory buatan kita
    private val viewModel: MainViewModel by viewModels {
        ViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 2. LOGIKA SESSION (Cek apakah user sudah login?)
        viewModel.getSession().observe(this) { isLoggedIn ->
            if (!isLoggedIn) {
                // Jika BELUM Login, pindah ke LoginActivity (Aktifkan baris bawah jika LoginActivity sudah ada)
                // val intent = Intent(this, LoginActivity::class.java)
                // startActivity(intent)
                // finish()

                Toast.makeText(this, "User Belum Login / Sesi Habis", Toast.LENGTH_SHORT).show()
            } else {
                // Jika SUDAH Login, biarkan di halaman ini
                Toast.makeText(this, "Selamat Datang Kembali!", Toast.LENGTH_SHORT).show()
            }
        }

        // Panggil refresh data (agar aplikasi mencoba download berita baru dari internet)
        viewModel.refreshData()

        setContent {
            DDwancanTheme {
                // 3. Mengamati Data Berita dari Database
                // 'observeAsState' mengubah LiveData menjadi State yang dimengerti Compose
                val newsList by viewModel.berita.observeAsState(initial = emptyList())

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Tampilkan List Berita
                    NewsScreen(
                        newsList = newsList,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

// 4. UI Sederhana untuk Menampilkan List Berita
@Composable
fun NewsScreen(newsList: List<NewsEntity>, modifier: Modifier = Modifier) {
    if (newsList.isEmpty()) {
        // Tampilan kalau data kosongn
        Box(modifier = modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            Text("Belum ada berita tersimpan.")
        }
    } else {
        // Tampilan List Berita (RecyclerView versi Compose)
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

@Composable
fun NewsItem(news: NewsEntity) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = news.title, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = news.publishedAt ?: "-", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = news.description ?: "Tidak ada deskripsi", style = MaterialTheme.typography.bodyMedium)
        }
    }
}