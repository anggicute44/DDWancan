package id.app.ddwancan.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.FirebaseFirestore
import id.app.ddwancan.data.model.Article
import id.app.ddwancan.data.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicInteger

class AdminNewsViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    val isLoading = mutableStateOf(false)
    val status = mutableStateOf("")

    fun updateNews(apiKey: String) {
        if (apiKey.isBlank()) {
            status.value = "39b789cf17324dc9bc343edb18ab7e24"
            return
        }

        isLoading.value = true
        status.value = "Memulai sinkronisasi..."

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.apiService.getTopHeadlines(category = null, apiKey = apiKey)
                val articles = response.articles

                if (articles.isNullOrEmpty()) {
                    status.value = "Tidak ada artikel ditemukan dari API"
                    isLoading.value = false
                    return@launch
                }

                // Queue semua write ke Firestore dan hitung hasilnya lewat listener
                val total = articles.size
                val successCount = AtomicInteger(0)
                val failCount = AtomicInteger(0)
                val doneCount = AtomicInteger(0)

                for (article in articles) {
                    val docId = article.url.hashCode().toString()
                    val data = hashMapOf<String, Any?>(
                        "title" to article.title,
                        "description" to article.description,
                        "author" to article.author,
                        "source_id" to article.source?.id,
                        "source_name" to article.source?.name,
                        "url" to article.url,
                        "urlToImage" to article.urlToImage,
                        "publishedAt" to article.publishedAt,
                        "importedAt" to FieldValue.serverTimestamp(),
                        "favoritesCount" to 0,
                        "favoritedBy" to emptyList<String>()
                    )

                    db.collection("News").document(docId).set(data, SetOptions.merge())
                        .addOnSuccessListener {
                            successCount.incrementAndGet()
                            if (doneCount.incrementAndGet() == total) {
                                status.value = "Selesai: ${successCount.get()} disimpan, ${failCount.get()} gagal"
                                isLoading.value = false
                            }
                        }
                        .addOnFailureListener {
                            failCount.incrementAndGet()
                            if (doneCount.incrementAndGet() == total) {
                                status.value = "Selesai: ${successCount.get()} disimpan, ${failCount.get()} gagal"
                                isLoading.value = false
                            }
                        }
                }
            } catch (e: Exception) {
                status.value = "Gagal ambil dari API: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }

    private fun saveArticleToFirestore(article: Article) {
        // Gunakan hash code URL sebagai ID dokumen yang cukup unik untuk kasus ini
        val docId = article.url.hashCode().toString()

        val data = hashMapOf<String, Any?>(
            "title" to article.title,
            "description" to article.description,
            "author" to article.author,
            "source_id" to article.source?.id,
            "source_name" to article.source?.name,
            "url" to article.url,
            "urlToImage" to article.urlToImage,
            "publishedAt" to article.publishedAt,
            "importedAt" to FieldValue.serverTimestamp(),
            "favoritesCount" to 0,
            "favoritedBy" to emptyList<String>()
        )

        // set() akan membuat/menimpa dokumen, gunakan merge agar tidak menghapus favorites yang sudah ada
        db.collection("News").document(docId).set(data, SetOptions.merge()).addOnFailureListener {
            // Jika gagal, biarkan exception terlempar supaya loop menghitungnya
            throw it
        }
    }
}
