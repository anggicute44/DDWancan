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
		// Observe local favorites stored in Room for offline-first display
		viewModelScope.launch {
			dbLocal.favoriteDao().getAllFavorites().collect { list ->
				_favorites.value = list.map { fav ->
					Article(
						source = null,
						author = null,
						title = fav.title ?: "",
						description = fav.description,
						url = fav.url,
						urlToImage = fav.urlToImage,
						publishedAt = fav.publishedAt ?: ""
					)
				}
			}
		}
	}

	fun addFavorite(articleUrl: String, onDone: () -> Unit = {}) {
		val uid = UserSession.userId ?: return
		viewModelScope.launch {
			try {
				// Insert local favorite immediately for instant UI feedback
				try {
					dbLocal.favoriteDao().insertFavorite(
						id.app.ddwancan.data.local.room.FavoriteEntity(url = articleUrl, title = null, description = null, urlToImage = null, publishedAt = null)
					)
				} catch (_: Exception) {}

				val pendingId = dbLocal.pendingFavoriteDao().insertPendingFavorite(
					PendingFavoriteEntity(articleUrl = articleUrl, userId = uid, action = "add")
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

	fun removeFavorite(articleUrl: String, onDone: () -> Unit = {}) {
		val uid = UserSession.userId ?: return
		viewModelScope.launch {
			try {
				// remove local immediately
				try { dbLocal.favoriteDao().deleteByUrl(articleUrl) } catch (_: Exception) {}
				// insert pending remove
				val pendingId = dbLocal.pendingFavoriteDao().insertPendingFavorite(
					PendingFavoriteEntity(articleUrl = articleUrl, userId = uid, action = "remove")
				)

				// try remote delete
				db.collection("Favorite").whereEqualTo("article_url", articleUrl).whereEqualTo("user_id", uid).get()
					.addOnSuccessListener { snap ->
						viewModelScope.launch {
							try {
								for (doc in snap.documents) {
									db.collection("Favorite").document(doc.id).delete()
								}
								// mark pending as synced
								try { dbLocal.pendingFavoriteDao().markAsSynced(pendingId) } catch (_: Exception) {}
							} catch (_: Exception) {}
							onDone()
						}
					}
					.addOnFailureListener {
						// leave pending for worker
						onDone()
					}
			} catch (e: Exception) {
				onDone()
			}
		}
	}
}
