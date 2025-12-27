package id.app.ddwancan.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [NewsEntity::class],
    version = 2, // ⬅️ NAIKKAN VERSION (WAJIB)
    exportSchema = false
)
abstract class NewsDatabase : RoomDatabase() {
    abstract fun newsDao(): NewsDao



    companion object {
        @Volatile
        private var INSTANCE: NewsDatabase? = null

        // Kode standar untuk membuat database Singleton
        fun getDatabase(context: Context): NewsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NewsDatabase::class.java,
                    "news_database" // Nama file database di HP user
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}