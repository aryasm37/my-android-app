package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.AppRepository
import com.example.db.HistoryItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(private val repository: AppRepository) : ViewModel() {

    val history = repository.allHistory.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val beepEnabled = repository.beepEnabled.stateIn(viewModelScope, SharingStarted.Eagerly, true)
    val vibrateEnabled = repository.vibrateEnabled.stateIn(viewModelScope, SharingStarted.Eagerly, true)
    val copyToClipboard = repository.copyToClipboard.stateIn(viewModelScope, SharingStarted.Eagerly, false)
    val colorScheme = repository.colorScheme.stateIn(viewModelScope, SharingStarted.Eagerly, "blue")

    private val _lastScannedItem = MutableStateFlow<HistoryItem?>(null)
    val lastScannedItem = _lastScannedItem.asStateFlow()

    fun setLastScannedItem(item: HistoryItem?) {
        _lastScannedItem.value = item
        if (item != null && item.id == 0) { // new item
            viewModelScope.launch {
                repository.insertHistory(item)
            }
        }
    }

    fun setBeepEnabled(enabled: Boolean) {
        viewModelScope.launch { repository.setBeepEnabled(enabled) }
    }

    fun setVibrateEnabled(enabled: Boolean) {
        viewModelScope.launch { repository.setVibrateEnabled(enabled) }
    }

    fun setCopyToClipboard(enabled: Boolean) {
        viewModelScope.launch { repository.setCopyToClipboard(enabled) }
    }

    fun setColorScheme(color: String) {
        viewModelScope.launch { repository.setColorScheme(color) }
    }

    class Factory(private val repository: AppRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(repository) as T
        }
    }
}
