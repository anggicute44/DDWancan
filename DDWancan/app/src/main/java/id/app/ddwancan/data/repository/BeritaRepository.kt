package id.app.ddwancan.data.repository

import id.app.ddwancan.data.local.NewsDao
import id.app.ddwancan.data.local.NewsEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BeritaRepository private constructor(
    private val newsDao: NewsDao
) {

    fun getAllBerita() = newsDao.getAllNews()

    suspend fun refreshBerita() {
        withContext(Dispatchers.IO) {

            // 🔥 DATA DUMMY (WAJIB BIAR UI HIDUP)
            val dummyNews = listOf(
                NewsEntity(
                    title = "UKDW Gelar Seminar AI",
                    description = "Seminar membahas penerapan AI di dunia pendidikan.",
                    imageUrl = null,
                    publishedAt = "27 Desember 2025",
                    content = "Fakultas TI UKDW mengadakan seminar AI..."
                ),
                NewsEntity(
                    title = "Mahasiswa Informatika Raih Prestasi",
                    description = "Tim mahasiswa berhasil menjuarai lomba nasional.",
                    imageUrl = null,
                    publishedAt = "26 Desember 2025",
                    content = "Prestasi ini diraih dalam ajang kompetisi nasional..."
                )
            )

            newsDao.deleteAll()
            newsDao.insertNews(dummyNews)
        }
    }

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
