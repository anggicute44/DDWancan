package id.app.ddwancan.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

// @Entity menandakan ini adalah tabel database
@Entity(tableName = "news_table")
data class NewsEntity(
    @PrimaryKey
    val title: String, // Kita pakai judul sebagai ID unik (atau bisa pakai ID dari API)

    val description: String?,
    val imageUrl: String?,
    val publishedAt: String?,
    val content: String?
)