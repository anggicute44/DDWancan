package id.app.ddwancan.viewmodel

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import id.app.ddwancan.data.local.room.AppDatabase
import id.app.ddwancan.data.local.room.PendingFavoriteEntity
import id.app.ddwancan.data.model.Article
import id.app.ddwancan.data.utils.UserSession
import kotlinx.coroutines.launch

class FavoriteViewModel(application: Application) : AndroidViewModel(application) {
	private val db = FirebaseFirestore.getInstance()
	private val dbLocal = AppDatabase.getInstance(application)

	private val _favorites = mutableStateOf<List<Article>>(emptyList())
	val favorites: State<List<Article>> = _favorites

	private val _isLoading = mutableStateOf(false)
	val isLoading: State<Boolean> = _isLoading

	private val _error = mutableStateOf<String?>(null)
	val error: State<String?> = _error

	fun loadFavorites() {
		val uid = UserSession.userId ?: return
		_isLoading.value = true
		db.collection("users").document(uid)
			.collection("favorites")
			.get()
			.addOnSuccessListener { result ->
				val list = mutableListOf<Article>()
				for (doc in result.documents) {
					val title = doc.getString("title") ?: ""
					val description = doc.getString("description")
					val url = doc.getString("url") ?: ""
					val urlToImage = doc.getString("urlToImage")
					val publishedAt = doc.getString("publishedAt") ?: ""

					val article = Article(
						source = null,
						author = null,
						title = title,
						description = description,
						url = url,
						urlToImage = urlToImage,
						publishedAt = publishedAt
					)
					list.add(article)
				}
				_favorites.value = list
				_isLoading.value = false
			}
			.addOnFailureListener { e ->
				_error.value = e.message
				_isLoading.value = false
			}
	}

	fun addFavorite(articleUrl: String, onDone: () -> Unit = {}) {
		val uid = UserSession.userId ?: return
		viewModelScope.launch {
			try {
				val pendingId = dbLocal.pendingFavoriteDao().insertPendingFavorite(
					PendingFavoriteEntity(articleUrl = articleUrl, userId = uid)
				)

				val data = hashMapOf(
					"user_id" to uid,
					"article_url" to articleUrl,
					"created_at" to com.google.firebase.firestore.FieldValue.serverTimestamp()
				)

				db.collection("Favorite").add(data)
					.addOnSuccessListener {
						viewModelScope.launch {
							try { dbLocal.pendingFavoriteDao().markAsSynced(pendingId) } catch (_: Exception) {}
						}
						onDone()
					}
					.addOnFailureListener { e ->
						// leave pending for worker
						onDone()
					}
			} catch (e: Exception) {
				onDone()
			}
		}
	}
}
