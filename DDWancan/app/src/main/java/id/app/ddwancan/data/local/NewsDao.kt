package id.app.ddwancan.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NewsDao {
    // 1. Ambil semua berita (Return Flow agar UI update otomatis)
    @Query("SELECT * FROM news_table ORDER BY publishedAt DESC")
    fun getAllNews(): Flow<List<NewsEntity>>

    // 2. Masukkan berita (REPLACE = Jika berita sudah ada, update isinya)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNews(news: List<NewsEntity>)

    // 3. Hapus semua berita (Opsional, misal user tarik refresh)
    @Query("DELETE FROM news_table")
    suspend fun deleteAll()
}