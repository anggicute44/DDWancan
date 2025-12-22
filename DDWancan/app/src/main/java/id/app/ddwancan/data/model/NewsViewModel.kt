package id.app.ddwancan.data.model

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class NewsViewModel : ViewModel() {
    private val _newsList = mutableStateOf<List<Article>>(emptyList())
    val newsList: State<List<Article>> = _newsList

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    fun fetchNews(category: String?) {
        // Sekarang ambil data dari Firestore (collection "News") agar user tidak memanggil API eksternal
        _isLoading.value = true
        _errorMessage.value = null

        val db = FirebaseFirestore.getInstance()
        db.collection("News")
            .get()
            .addOnSuccessListener { result ->
                val list = mutableListOf<Article>()
                for (doc in result.documents) {
                    val sourceId = doc.getString("source_id")
                    val sourceName = doc.getString("source_name") ?: ""
                    val source = Source(sourceId, sourceName)

                    val article = Article(
                        source = source,
                        author = doc.getString("author"),
                        title = doc.getString("title") ?: "",
                        description = doc.getString("description"),
                        url = doc.getString("url") ?: "",
                        urlToImage = doc.getString("urlToImage"),
                        publishedAt = doc.getString("publishedAt") ?: ""
                    )
                    list.add(article)
                }
                _newsList.value = list
                _isLoading.value = false
            }
            .addOnFailureListener { e ->
                _errorMessage.value = e.message
                _isLoading.value = false
            }
    }
}

