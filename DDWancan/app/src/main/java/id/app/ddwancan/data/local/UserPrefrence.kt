package id.app.ddwancan.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Membuat ekstensi dataStore agar bisa diakses dari Context manapun
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

class UserPreference private constructor(private val dataStore: DataStore<Preferences>) {

    // Key untuk menyimpan data (seperti nama variabel di database kecil)
    private val TOKEN_KEY = stringPreferencesKey("token")
    private val IS_LOGIN_KEY = booleanPreferencesKey("is_login")

    // 1. Fungsi untuk mendapatkan data sesi (apakah user sedang login?)
    // Mengembalikan Flow agar UI bisa memantau perubahan secara realtime
    fun getSession(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[IS_LOGIN_KEY] ?: false // Defaultnya false (belum login)
        }
    }

    // Fungsi tambahan jika butuh ambil Token
    fun getToken(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[TOKEN_KEY] ?: ""
        }
    }

    // 2. Fungsi untuk menyimpan sesi saat Login Berhasil
    suspend fun saveSession(token: String) {
        dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
            preferences[IS_LOGIN_KEY] = true
        }
    }

    // 3. Fungsi untuk Logout (Hapus data sesi)
    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences.clear() // Hapus semua data
        }
    }

    // Pola Singleton (Agar hanya ada satu instance UserPreference di seluruh aplikasi)
    companion object {
        @Volatile
        private var INSTANCE: UserPreference? = null

        fun getInstance(dataStore: DataStore<Preferences>): UserPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}