package id.app.ddwancan.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [ArticleEntity::class, PendingFavoriteEntity::class, PendingCommentEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun articleDao(): ArticleDao
    abstract fun pendingFavoriteDao(): PendingFavoriteDao
    abstract fun pendingCommentDao(): PendingCommentDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ddwancan_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
