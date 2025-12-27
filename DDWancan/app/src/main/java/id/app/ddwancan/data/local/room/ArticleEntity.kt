package id.app.ddwancan.data.local.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "articles")
data class ArticleEntity(
    @PrimaryKey
    @ColumnInfo(name = "url") val url: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "description") val description: String?,
    @ColumnInfo(name = "author") val author: String?,
    @ColumnInfo(name = "urlToImage") val urlToImage: String?,
    @ColumnInfo(name = "publishedAt") val publishedAt: String?,
    @ColumnInfo(name = "source_id") val sourceId: String?,
    @ColumnInfo(name = "source_name") val sourceName: String?
    ,
    @ColumnInfo(name = "favoritesCount") val favoritesCount: Int = 0
)
