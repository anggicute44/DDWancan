package id.app.ddwancan.data.model

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class NewsViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _newsList = mutableStateOf<List<Article>>(emptyList())
    val newsList: State<List<Article>> = _newsList

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    /**
     * Fetch articles from Firestore `News` collection.
     * If `category` is provided, apply a simple client-side filter that
     * checks whether the title/description contains the category keyword.
     */
    fun fetchNews(category: String?) {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                db.collection("News").get()
                    .addOnSuccessListener { snapshot ->
                        val list = snapshot.documents.mapNotNull { doc ->
                            val sourceId = doc.getString("source_id")
                            val sourceName = doc.getString("source_name")
                            val author = doc.getString("author")
                            val title = doc.getString("title") ?: return@mapNotNull null
                            val description = doc.getString("description")
                            val url = doc.getString("url") ?: return@mapNotNull null
                            val urlToImage = doc.getString("urlToImage")
                            val publishedAt = doc.getString("publishedAt") ?: ""

                            Article(
                                source = if (sourceId != null || sourceName != null) Source(id = sourceId, name = sourceName ?: "") else null,
                                author = author,
                                title = title,
                                description = description,
                                url = url,
                                urlToImage = urlToImage,
                                publishedAt = publishedAt
                            )
                        }

                        // Apply category client-side if requested (fallback when documents lack category field)
                        val filtered = if (category.isNullOrBlank()) {
                            list
                        } else {
                            val key = category.lowercase()
                            list.filter { art ->
                                (art.title.lowercase().contains(key)) || (art.description?.lowercase()?.contains(key) == true)
                            }
                        }

                        // Sort by publishedAt if possible (newest first). publishedAt expected like "2025-12-21T..."
                        val sorted = filtered.sortedByDescending { it.publishedAt }
                        _newsList.value = sorted
                        _isLoading.value = false
                    }
                    .addOnFailureListener { e ->
                        _errorMessage.value = e.message
                        _isLoading.value = false
                    }
            } catch (e: Exception) {
                _errorMessage.value = e.message
                _isLoading.value = false
            }
        }
    }
}

