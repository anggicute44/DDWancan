package id.app.ddwancan.data.model

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.app.ddwancan.data.network.RetrofitClient
import kotlinx.coroutines.launch

class NewsViewModel : ViewModel() {

    // State untuk menyimpan list berita
    private val _newsList = mutableStateOf<List<Article>>(emptyList())
    val newsList: State<List<Article>> = _newsList

    // State untuk loading
    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    // State untuk error message
    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    init {
        fetchNews()
    }

    private fun fetchNews() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Ganti API KEY di sini
                val response = RetrofitClient.apiService.getTopHeadlines(apiKey = "39b789cf17324dc9bc343edb18ab7e24")
                _newsList.value = response.articles
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}