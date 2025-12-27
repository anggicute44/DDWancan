package id.app.ddwancan.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import id.app.ddwancan.data.local.NewsEntity
import id.app.ddwancan.data.local.UserPreference
import id.app.ddwancan.data.repository.BeritaRepository
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: BeritaRepository,
    private val preference: UserPreference
) : ViewModel() {

    // ======================
    // SESSION LOGIN
    // ======================
    fun getSession(): LiveData<Boolean> {
        return preference.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            preference.logout()
        }
    }

    // ======================
    // BERITA (ROOM)
    // ======================
    val berita: LiveData<List<NewsEntity>> =
        repository.getAllBerita().asLiveData()

    // ======================
    // REFRESH DARI API
    // ======================
    fun refreshData() {
        viewModelScope.launch {
            try {
                Log.d("MainViewModel", "Mulai refresh berita...")
                repository.refreshBerita()
                Log.d("MainViewModel", "Refresh berita selesai")
            } catch (e: Exception) {
                Log.e("MainViewModel", "Gagal refresh berita", e)
            }
        }
    }
}
