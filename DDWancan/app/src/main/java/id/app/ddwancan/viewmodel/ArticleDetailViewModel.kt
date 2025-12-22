package id.app.ddwancan.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import id.app.ddwancan.data.utils.ArticleUtils

class ArticleDetailViewModel : ViewModel() {

	private val db = FirebaseFirestore.getInstance()

	val isFavorited = mutableStateOf(false)
	val favoritesCount = mutableStateOf(0)
	val isLoading = mutableStateOf(false)
	val error = mutableStateOf<String?>(null)

	fun loadFavoriteState(articleUrl: String, userId: String?) {
		if (articleUrl.isBlank()) return

		val docId = ArticleUtils.docIdFromUrl(articleUrl)
		db.collection("News").document(docId).get()
			.addOnSuccessListener { doc ->
				if (doc.exists()) {
					favoritesCount.value = (doc.getLong("favoritesCount")?.toInt() ?: 0)
					val list = doc.get("favoritedBy") as? List<*>
					isFavorited.value = if (userId == null) false else list?.contains(userId) == true
				} else {
					favoritesCount.value = 0
					isFavorited.value = false
				}
			}
			.addOnFailureListener { e ->
				error.value = e.message
			}
	}

	fun toggleFavorite(articleUrl: String, userId: String?) {
		if (userId.isNullOrBlank()) {
			error.value = "Silakan login untuk memberi favorite"
			return
		}

		val docId = ArticleUtils.docIdFromUrl(articleUrl)
		val docRef = db.collection("News").document(docId)

		// Ambil dokumen dulu untuk tahu apakah user sudah memberi favorite
		isLoading.value = true
		docRef.get()
			.addOnSuccessListener { doc ->
				val list = doc.get("favoritedBy") as? List<String>
				val hasFav = list?.contains(userId) == true

				if (hasFav) {
					// Remove favorite
					docRef.update(
						"favoritesCount", FieldValue.increment(-1),
						"favoritedBy", FieldValue.arrayRemove(userId)
					).addOnSuccessListener {
						isFavorited.value = false
						favoritesCount.value = (favoritesCount.value - 1).coerceAtLeast(0)
						isLoading.value = false
					}.addOnFailureListener { e ->
						error.value = e.message
						isLoading.value = false
					}
				} else {
					// Add favorite
					docRef.update(
						"favoritesCount", FieldValue.increment(1),
						"favoritedBy", FieldValue.arrayUnion(userId)
					).addOnSuccessListener {
						isFavorited.value = true
						favoritesCount.value = favoritesCount.value + 1
						isLoading.value = false
					}.addOnFailureListener { e ->
						// Jika dokumen belum ada, buat dokumen baru dengan field yang dibutuhkan
						docRef.set(
							mapOf(
								"favoritesCount" to 1,
								"favoritedBy" to listOf(userId)
							)
						).addOnSuccessListener {
							isFavorited.value = true
							favoritesCount.value = 1
							isLoading.value = false
						}.addOnFailureListener { ex ->
							error.value = ex.message
							isLoading.value = false
						}
					}
				}
			}
			.addOnFailureListener { e ->
				error.value = e.message
				isLoading.value = false
			}
	}
}