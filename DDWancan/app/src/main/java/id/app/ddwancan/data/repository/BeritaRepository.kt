package id.app.ddwancan.data.repository

import id.app.ddwancan.data.local.NewsDao
import id.app.ddwancan.data.local.NewsEntity
import kotlinx.coroutines.flow.Flow

class BeritaRepository private constructor(
    private val newsDao: NewsDao
    // private val apiService: ApiService (Nanti kita buka komen ini kalau Retrofit sudah masuk)
) {

    // 1. Ambil Berita (Langsung dari Database Lokal agar Offline Jalan)
    fun getAllBerita(): Flow<List<NewsEntity>> {
        return newsDao.getAllNews()
    }

    // 2. Fungsi Refresh (Ambil dari Internet -> Simpan ke Database)
    // Dipanggil saat aplikasi dibuka atau user swipe-to-refresh
    suspend fun refreshBerita() {
        try {
            // --- LOGIKA UPDATE DATA NANTI DI SINI ---

            // 1. val response = apiService.getBerita()
            // 2. val dataBaru = response.articles.map { ... konversi ke NewsEntity ... }

            // 3. Simpan ke database (ini akan otomatis update layar HP user)
            // newsDao.deleteAll()
            // newsDao.insertNews(dataBaru)

        } catch (e: Exception) {
            // Kalau internet mati, diam saja. User tetap baca data lama dari database.
        }
    }

    // Pola Singleton (Agar Repository ini cuma ada satu di seluruh aplikasi)
    companion object {
        @Volatile
        private var instance: BeritaRepository? = null

        fun getInstance(newsDao: NewsDao): BeritaRepository {
            return instance ?: synchronized(this) {
                instance ?: BeritaRepository(newsDao).also { instance = it }
            }
        }
    }
}