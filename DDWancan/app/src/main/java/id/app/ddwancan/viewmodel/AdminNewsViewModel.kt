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

    // Default API key (used when admin doesn't input one)
    private val DEFAULT_API_KEY = "39b789cf17324dc9bc343edb18ab7e24"

    // Preset queries available for admin
    enum class Preset(val label: String) {
        EVERYTHING_APPLE("Everything: q=apple, from=2025-12-21, to=2025-12-21, sortBy=popularity"),
        EVERYTHING_TESLA("Everything: q=tesla, from=2025-11-22, sortBy=publishedAt"),
        TOP_HEADLINES_US_BUSINESS("Top Headlines: country=us, category=business"),
        TOP_HEADLINES_TECHCRUNCH("Top Headlines: sources=techcrunch"),
        EVERYTHING_WSJ("Everything: domains=wsj.com")
    }

    /**
     * Update news using a preset. Admin selects which preset to run.
     * Uses DEFAULT_API_KEY when not specified by presets.
     */
    fun updateNewsWithPreset(preset: Preset) {
        val apiKey = DEFAULT_API_KEY

        isLoading.value = true
        status.value = "Memulai sinkronisasi (${preset.name})..."

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = when (preset) {
                    Preset.EVERYTHING_APPLE -> RetrofitClient.apiService.getEverything(
                        q = "apple",
                        from = "2025-12-21",
                        to = "2025-12-21",
                        sortBy = "popularity",
                        apiKey = apiKey
                    )
                    Preset.EVERYTHING_TESLA -> RetrofitClient.apiService.getEverything(
                        q = "tesla",
                        from = "2025-11-22",
                        sortBy = "publishedAt",
                        apiKey = apiKey
                    )
                    Preset.TOP_HEADLINES_US_BUSINESS -> RetrofitClient.apiService.getTopHeadlines(
                        country = "us",
                        category = "business",
                        apiKey = apiKey
                    )
                    Preset.TOP_HEADLINES_TECHCRUNCH -> RetrofitClient.apiService.getTopHeadlines(
                        sources = "techcrunch",
                        country = "",
                        category = "",
                        apiKey = apiKey
                    )
                    Preset.EVERYTHING_WSJ -> RetrofitClient.apiService.getEverything(
                        domains = "wsj.com",
                        apiKey = apiKey
                    )
                }

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
                    try {
                        saveArticleToFirestore(article)
                        successCount.incrementAndGet()
                    } catch (e: Exception) {
                        failCount.incrementAndGet()
                    } finally {
                        if (doneCount.incrementAndGet() == total) {
                            status.value = "Selesai: ${successCount.get()} disimpan, ${failCount.get()} gagal"
                            isLoading.value = false
                        }
                    }
                }
            } catch (e: Exception) {
                status.value = "Gagal ambil dari API: ${e.message}"
                isLoading.value = false
            }
        }
    }

    private fun saveArticleToFirestore(article: Article) {
        // Gunakan UUID deterministik berdasarkan URL sehingga identik untuk URL sama
        val docId = java.util.UUID.nameUUIDFromBytes(article.url.toByteArray()).toString()

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