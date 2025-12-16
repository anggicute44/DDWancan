package id.app.ddwancan.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WelcomeViewModel : ViewModel() {

    // StateFlow untuk menandakan kapan harus navigasi ke halaman Login.
    // Dibuat private agar hanya bisa diubah dari dalam ViewModel.
    private val _navigateToLogin = MutableStateFlow(false)

    // StateFlow publik yang hanya bisa dibaca (read-only) oleh UI.
    val navigateToLogin = _navigateToLogin.asStateFlow()

    init {
        // Memulai coroutine di dalam viewModelScope.
        // Coroutine ini akan otomatis dibatalkan saat ViewModel dihancurkan.
        viewModelScope.launch {
            // Memberi jeda selama 3 detik (3000 milidetik) untuk menampilkan welcome screen.
            delay(3000)
            // Setelah jeda selesai, ubah nilai state untuk memicu navigasi.
            _navigateToLogin.value = true
        }
    }

    /**
     * Fungsi ini dipanggil dari UI setelah navigasi selesai dilakukan.
     * Tujuannya adalah untuk mereset state agar tidak terjadi navigasi berulang
     * saat terjadi perubahan konfigurasi (misalnya rotasi layar).
     */
    fun onNavigationDone() {
        _navigateToLogin.value = false
    }
}
