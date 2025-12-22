package id.app.ddwancan.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import id.app.ddwancan.data.model.Article
import id.app.ddwancan.data.utils.UserSession

class FavoriteViewModel : ViewModel() {
	private val db = FirebaseFirestore.getInstance()

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
}