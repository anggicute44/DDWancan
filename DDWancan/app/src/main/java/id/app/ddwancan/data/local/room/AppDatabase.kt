package id.app.ddwancan.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [ArticleEntity::class, PendingFavoriteEntity::class, PendingCommentEntity::class, FavoriteEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun articleDao(): ArticleDao
    abstract fun pendingFavoriteDao(): PendingFavoriteDao
    abstract fun pendingCommentDao(): PendingCommentDao
    abstract fun favoriteDao(): FavoriteDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ddwancan_db"
                ).fallbackToDestructiveMigration().build()
                // Also allow destructive migration on downgrade to avoid runtime crashes during development
                // (In production, provide proper Migration objects instead.)
                INSTANCE = instance
                instance
            }
        }
    }
}
