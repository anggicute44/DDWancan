package id.app.ddwancan.data.repository

import android.content.Context
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import id.app.ddwancan.data.local.room.AppDatabase
import id.app.ddwancan.data.local.room.ArticleEntity
import id.app.ddwancan.data.local.room.PendingCommentEntity
import id.app.ddwancan.data.local.room.PendingFavoriteEntity
import id.app.ddwancan.data.model.Article
import id.app.ddwancan.data.model.Source
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class NewsRepository(private val context: Context) {
    private val db = FirebaseFirestore.getInstance()
    private val database = AppDatabase.getInstance(context)
    private val articleDao = database.articleDao()
    private val pendingFavoriteDao = database.pendingFavoriteDao()
    private val pendingCommentDao = database.pendingCommentDao()

    fun getArticlesFlow(): Flow<List<Article>> {
        return articleDao.getAllArticles().map { list ->
            list.map { it.toModel() }
        }
    }

    suspend fun refreshFromRemote() {
        val snap = db.collection("News").get().await()
        val entities = snap.documents.mapNotNull { doc ->
            val sourceId = doc.getString("source_id")
            val sourceName = doc.getString("source_name")
            val author = doc.getString("author")
            val title = doc.getString("title") ?: return@mapNotNull null
            val description = doc.getString("description")
            val url = doc.getString("url") ?: return@mapNotNull null
            val urlToImage = doc.getString("urlToImage")
            val publishedAt = doc.getString("publishedAt") ?: ""

            ArticleEntity(
                url = url,
                title = title,
                description = description,
                author = author,
                urlToImage = urlToImage,
                publishedAt = publishedAt,
                sourceId = sourceId,
                sourceName = sourceName
            )
        }

        if (entities.isNotEmpty()) {
            articleDao.upsertArticles(entities)
        }
    }

    suspend fun syncPending(userId: String?) {
        // Upload pending favorites
        val pendingFavs: List<PendingFavoriteEntity> = pendingFavoriteDao.getPendingFavoritesOnce()
        for (fav in pendingFavs) {
            try {
                val data = hashMapOf(
                    "user_id" to fav.userId,
                    "article_url" to fav.articleUrl,
                    "created_at" to FieldValue.serverTimestamp()
                )
                db.collection("Favorite").add(data).await()
                // mark as synced
                pendingFavoriteDao.markAsSynced(fav.id)
            } catch (e: Exception) {
                // continue, will retry later
            }
        }

        // Upload pending comments
        val pendingComments: List<PendingCommentEntity> = pendingCommentDao.getPendingCommentsOnce()
        for (c in pendingComments) {
            try {
                val data = hashMapOf(
                    "id_user" to c.userId,
                    "komentar" to c.content,
                    "waktu" to FieldValue.serverTimestamp(),
                    "article_url" to c.articleUrl,
                    "source_id" to null,
                    "warningTotal" to 0,
                    "status" to "ok"
                )
                db.collection("Comment").add(data).await()
                pendingCommentDao.markAsSynced(c.id)
            } catch (e: Exception) {
                // ignore and retry later
            }
        }
    }
}

// Mapper extension
private fun ArticleEntity.toModel(): Article {
    return Article(
        source = if (this.sourceId != null || this.sourceName != null) Source(id = this.sourceId, name = this.sourceName ?: "") else null,
        author = this.author,
        title = this.title,
        description = this.description,
        url = this.url,
        urlToImage = this.urlToImage,
        publishedAt = this.publishedAt ?: ""
    )
}
