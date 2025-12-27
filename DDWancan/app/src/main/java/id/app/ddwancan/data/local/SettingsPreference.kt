package id.app.ddwancan.data.local


import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.settingsDataStore by preferencesDataStore(name = "settings")

class SettingsPreference(private val context: Context) {

    private val DARK_MODE = booleanPreferencesKey("dark_mode")
    private val LANGUAGE_EN = booleanPreferencesKey("language_en")

    val isDarkMode: Flow<Boolean> =
        context.settingsDataStore.data.map { it[DARK_MODE] ?: false }

    val isEnglish: Flow<Boolean> =
        context.settingsDataStore.data.map { it[LANGUAGE_EN] ?: false }

    suspend fun saveDarkMode(value: Boolean) {
        context.settingsDataStore.edit {
            it[DARK_MODE] = value
        }
    }

    suspend fun saveLanguage(value: Boolean) {
        context.settingsDataStore.edit {
            it[LANGUAGE_EN] = value
        }
    }
}
