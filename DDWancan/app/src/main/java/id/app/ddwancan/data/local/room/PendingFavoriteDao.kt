package id.app.ddwancan.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PendingFavoriteDao {
    @Query("SELECT * FROM pending_favorites WHERE is_synced = 0 ORDER BY created_at ASC")
    fun getPendingFavorites(): Flow<List<PendingFavoriteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPendingFavorite(favorite: PendingFavoriteEntity): Long

    @Query("UPDATE pending_favorites SET is_synced = 1 WHERE id = :id")
    suspend fun markAsSynced(id: Long)

    @Query("SELECT * FROM pending_favorites WHERE is_synced = 0 ORDER BY created_at ASC")
    suspend fun getPendingFavoritesOnce(): List<PendingFavoriteEntity>
}
