package id.app.ddwancan.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import id.app.ddwancan.data.local.NewsDatabase
import id.app.ddwancan.data.local.UserPreference
import id.app.ddwancan.data.local.dataStore
import id.app.ddwancan.data.repository.BeritaRepository

class ViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {

            // ✅ GUNAKAN application SEBAGAI CONTEXT
            val database = NewsDatabase.getDatabase(application)
            val repository = BeritaRepository.getInstance(database.newsDao())
            val preference = UserPreference.getInstance(application.dataStore)

            return MainViewModel(repository, preference) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}
