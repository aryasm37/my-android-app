package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.db.HistoryDao
import com.example.db.HistoryItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class AppRepository(
    private val historyDao: HistoryDao,
    private val dataStore: DataStore<Preferences>
) {
    val allHistory: Flow<List<HistoryItem>> = historyDao.getAllHistory()

    suspend fun insertHistory(item: HistoryItem) = historyDao.insert(item)

    suspend fun deleteHistory(id: Int) = historyDao.deleteById(id)
    
    suspend fun clearHistory() = historyDao.clearAll()

    // Settings
    val beepEnabled: Flow<Boolean> = dataStore.data.map { it[BEEP_KEY] ?: true }
    val vibrateEnabled: Flow<Boolean> = dataStore.data.map { it[VIBRATE_KEY] ?: true }
    val copyToClipboard: Flow<Boolean> = dataStore.data.map { it[COPY_KEY] ?: false }
    val colorScheme: Flow<String> = dataStore.data.map { it[COLOR_KEY] ?: "blue" }

    suspend fun setBeepEnabled(enabled: Boolean) {
        dataStore.edit { it[BEEP_KEY] = enabled }
    }
    
    suspend fun setVibrateEnabled(enabled: Boolean) {
        dataStore.edit { it[VIBRATE_KEY] = enabled }
    }

    suspend fun setCopyToClipboard(enabled: Boolean) {
        dataStore.edit { it[COPY_KEY] = enabled }
    }

    suspend fun setColorScheme(color: String) {
        dataStore.edit { it[COLOR_KEY] = color }
    }

    companion object {
        val BEEP_KEY = booleanPreferencesKey("beep_enabled")
        val VIBRATE_KEY = booleanPreferencesKey("vibrate_enabled")
        val COPY_KEY = booleanPreferencesKey("copy_to_clipboard")
        val COLOR_KEY = stringPreferencesKey("color_scheme")
    }
}
