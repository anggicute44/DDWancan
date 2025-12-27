package id.app.ddwancan.data.model

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import id.app.ddwancan.data.repository.NewsRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class NewsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = NewsRepository(application.applicationContext)

    private val _newsList = mutableStateOf<List<Article>>(emptyList())
    val newsList: State<List<Article>> = _newsList

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    init {
        // Observe local DB and emit to UI
        viewModelScope.launch {
            repository.getArticlesFlow().collectLatest { list ->
                _newsList.value = list
            }
        }
        // Kick off a background refresh from remote
        refreshFromRemote()
    }

    fun refreshFromRemote() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.refreshFromRemote()
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun syncPending(userId: String?) {
        viewModelScope.launch {
            repository.syncPending(userId)
        }
    }
}

