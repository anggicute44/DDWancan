package id.app.ddwancan.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import id.app.ddwancan.data.local.room.AppDatabase
import id.app.ddwancan.data.local.room.FavoriteEntity
import id.app.ddwancan.data.local.room.PendingFavoriteEntity
import id.app.ddwancan.data.utils.ArticleUtils
import kotlinx.coroutines.launch

class ArticleDetailViewModel(application: Application) : AndroidViewModel(application) {

	private val db = FirebaseFirestore.getInstance()
	private val dbLocal = AppDatabase.getInstance(application)

	val isFavorited = mutableStateOf(false)
	val favoritesCount = mutableStateOf(0)
	val isLoading = mutableStateOf(false)
	val error = mutableStateOf<String?>(null)

	fun loadFavoriteState(articleUrl: String, userId: String?) {
		if (articleUrl.isBlank()) return

		val docId = ArticleUtils.docIdFromUrl(articleUrl)

		// Prefer local cached favoritesCount first (optimistic/offline). Then fetch remote and merge.
		viewModelScope.launch {
			try {
				val localCount = try { dbLocal.articleDao().getFavoritesCount(articleUrl) } catch (_: Exception) { null }
				if (localCount != null) {
					favoritesCount.value = localCount
				} else {
					favoritesCount.value = 0
				}
			} catch (_: Exception) {
				favoritesCount.value = 0
			}
		}

		// Load favoritesCount from remote (best-effort) and take the max(remote, local)
		db.collection("News").document(docId).get()
			.addOnSuccessListener { doc ->
				val remote = if (doc.exists()) (doc.getLong("favoritesCount")?.toInt() ?: 0) else 0
				val current = favoritesCount.value
				favoritesCount.value = maxOf(current, remote)
			}
			.addOnFailureListener { e -> error.value = e.message }

		// Check local favorites for favorited state
		viewModelScope.launch {
			try {
				val count = dbLocal.favoriteDao().isFavoritedOnce(articleUrl)
				isFavorited.value = count > 0
			} catch (e: Exception) {
				isFavorited.value = false
			}
		}
	}

	fun toggleFavorite(
		articleUrl: String,
		userId: String?,
		title: String? = null,
		description: String? = null,
		imageUrl: String? = null,
		publishedAt: String? = null
	) {
		if (userId.isNullOrBlank()) {
			error.value = "Silakan login untuk memberi favorite"
			return
		}

		isLoading.value = true

		viewModelScope.launch {
			try {
				val currently = dbLocal.favoriteDao().isFavoritedOnce(articleUrl) > 0

				if (currently) {
					// remove local immediately and try remove remote; queue pending remove on failure
					try { dbLocal.favoriteDao().deleteByUrl(articleUrl) } catch (_: Exception) {}
					isFavorited.value = false
					// optimistic decrement so UI shows change while offline
					favoritesCount.value = (favoritesCount.value - 1).coerceAtLeast(0)
					// persist optimistic count to local articles table so list reflects change
					viewModelScope.launch {
						try { dbLocal.articleDao().updateFavoritesCount(articleUrl, favoritesCount.value) } catch (_: Exception) {}
					}
					val docId = ArticleUtils.docIdFromUrl(articleUrl)
					try {
						// attempt to delete user's favorite doc and update News counters
						if (!userId.isNullOrBlank()) {
							db.collection("users").document(userId).collection("favorites").document(docId).delete().addOnFailureListener { }
						}
						db.collection("News").document(docId)
							.update(
								"favoritesCount", FieldValue.increment(-1),
								"favoritedBy", FieldValue.arrayRemove(userId)
							)
							.addOnSuccessListener {
								isFavorited.value = false
								favoritesCount.value = (favoritesCount.value - 1).coerceAtLeast(0)
								isLoading.value = false
							}
							.addOnFailureListener {
								// queue pending remove
								viewModelScope.launch {
									val pending = PendingFavoriteEntity(articleUrl = articleUrl, userId = userId, action = "remove")
									dbLocal.pendingFavoriteDao().insertPendingFavorite(pending)
									isLoading.value = false
								}
							}
					} catch (e: Exception) {
						// queue pending remove if any exception
						viewModelScope.launch {
							val pending = PendingFavoriteEntity(articleUrl = articleUrl, userId = userId, action = "remove")
							dbLocal.pendingFavoriteDao().insertPendingFavorite(pending)
							isLoading.value = false
						}
					}
				} else {
					// Add favorite locally + pending
					val fav = FavoriteEntity(url = articleUrl, title = title, description = description, urlToImage = imageUrl, publishedAt = publishedAt)
					dbLocal.favoriteDao().insertFavorite(fav)
					isFavorited.value = true
					// optimistic increment so UI shows change while offline
					favoritesCount.value = favoritesCount.value + 1
					// persist optimistic count to local articles table so list reflects change
					viewModelScope.launch {
						try { dbLocal.articleDao().updateFavoritesCount(articleUrl, favoritesCount.value) } catch (_: Exception) {}
					}

					val pending = PendingFavoriteEntity(articleUrl = articleUrl, userId = userId, action = "add")
					dbLocal.pendingFavoriteDao().insertPendingFavorite(pending)

					// Try immediate remote update
					val docId = ArticleUtils.docIdFromUrl(articleUrl)
					try {
						db.collection("News").document(docId)
							.update(
								"favoritesCount", FieldValue.increment(1),
								"favoritedBy", FieldValue.arrayUnion(userId)
							)
							.addOnSuccessListener {
								// Also add to user's favorites collection
								try {
									val favData = mapOf(
										"title" to (title ?: ""),
										"description" to (description ?: ""),
										"url" to articleUrl,
										"urlToImage" to (imageUrl ?: ""),
										"publishedAt" to (publishedAt ?: "")
									)
									db.collection("users").document(userId).collection("favorites").document(docId).set(favData)
								} catch (_: Exception) {}
								// mark pending as synced
								viewModelScope.launch {
									try { dbLocal.pendingFavoriteDao().markAsSyncedByArticle(articleUrl) } catch (_: Exception) {}
									isFavorited.value = true
									favoritesCount.value = favoritesCount.value + 1
									isLoading.value = false
								}
							}
							.addOnFailureListener {
								// If update failed, leave pending; UI already shows favorited locally
								isFavorited.value = true
								favoritesCount.value = favoritesCount.value + 1
								isLoading.value = false
							}
					} catch (e: Exception) {
						// leave pending
						isFavorited.value = true
						favoritesCount.value = favoritesCount.value + 1
						isLoading.value = false
					}
				}
			} catch (e: Exception) {
				error.value = e.message
				isLoading.value = false
			}
		}
	}
}