package id.app.ddwancan.viewmodel // Sesuaikan package

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

    // --- BAGIAN 1: SESSION LOGIN ---

    // Mengambil status login (Live)
    // Activity akan memantau ini: Jika true -> Home, Jika false -> Login
    fun getSession(): LiveData<Boolean> {
        return preference.getSession().asLiveData()
    }

    // Fungsi Logout
    fun logout() {
        viewModelScope.launch {
            preference.logout()
        }
    }

    // --- BAGIAN 2: BERITA OFFLINE ---

    // Mengambil data berita dari database (Live)
    val berita: LiveData<List<NewsEntity>> = repository.getAllBerita().asLiveData()

    // Fungsi untuk update data dari internet (Dipanggil saat refresh)
    fun refreshData() {
        viewModelScope.launch {
            repository.refreshBerita()
        }
    }
}