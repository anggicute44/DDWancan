package id.app.ddwancan.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PendingCommentDao {
    @Query("SELECT * FROM pending_comments WHERE is_synced = 0 ORDER BY created_at ASC")
    fun getPendingComments(): Flow<List<PendingCommentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPendingComment(comment: PendingCommentEntity): Long

    @Query("UPDATE pending_comments SET is_synced = 1 WHERE id = :id")
    suspend fun markAsSynced(id: Long)

    @Query("SELECT * FROM pending_comments WHERE is_synced = 0 ORDER BY created_at ASC")
    suspend fun getPendingCommentsOnce(): List<PendingCommentEntity>
}
