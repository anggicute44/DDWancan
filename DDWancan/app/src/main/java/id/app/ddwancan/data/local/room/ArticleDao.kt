package id.app.ddwancan.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDao {
    @Query("SELECT * FROM articles ORDER BY publishedAt DESC")
    fun getAllArticles(): Flow<List<ArticleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertArticles(articles: List<ArticleEntity>)

    @Query("DELETE FROM articles")
    suspend fun clearAll()

    @Query("UPDATE articles SET favoritesCount = :count WHERE url = :url")
    suspend fun updateFavoritesCount(url: String, count: Int)
}
