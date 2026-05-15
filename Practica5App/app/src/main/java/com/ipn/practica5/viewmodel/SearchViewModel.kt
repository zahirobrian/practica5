package com.ipn.practica5.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.ipn.practica5.App
import com.ipn.practica5.data.local.entity.MediaItem
import com.ipn.practica5.data.local.entity.SearchHistory
import kotlinx.coroutines.launch

class SearchViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = (application as App).repository

    private val _results = MutableLiveData<List<MediaItem>>()
    val results: LiveData<List<MediaItem>> = _results

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isOffline = MutableLiveData(false)
    val isOffline: LiveData<Boolean> = _isOffline

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    var currentUserId: String = ""

    private var _history: LiveData<List<SearchHistory>>? = null
    fun getHistory(): LiveData<List<SearchHistory>> {
        if (_history == null) _history = repo.getSearchHistory(currentUserId)
        return _history!!
    }

    fun searchBooks(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _isOffline.value = !repo.isOnline()
            _error.value = null
            try {
                _results.value = repo.searchBooks(query, currentUserId)
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun searchShows(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _isOffline.value = !repo.isOnline()
            _error.value = null
            try {
                _results.value = repo.searchShows(query, currentUserId)
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearHistory() = viewModelScope.launch { repo.clearHistory(currentUserId) }
}
