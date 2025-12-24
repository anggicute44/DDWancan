package id.app.ddwancan.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import id.app.ddwancan.data.local.NewsDatabase
import id.app.ddwancan.data.local.UserPreference
import id.app.ddwancan.data.local.dataStore
import id.app.ddwancan.data.repository.BeritaRepository

// Menggunakan ViewModelProvider.Factory (Interface) lebih umum daripada NewInstanceFactory
class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {

            // --- MANUAL INJECTION ---
            val database = NewsDatabase.getDatabase(context)
            val repository = BeritaRepository.getInstance(database.newsDao())
            val preference = UserPreference.getInstance(context.dataStore)

            // Masukkan ke ViewModel
            return MainViewModel(repository, preference) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}