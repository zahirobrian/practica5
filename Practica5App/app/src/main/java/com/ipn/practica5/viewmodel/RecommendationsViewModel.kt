package com.ipn.practica5.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.ipn.practica5.App
import com.ipn.practica5.data.local.entity.MediaItem
import kotlinx.coroutines.launch

class RecommendationsViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = (application as App).repository

    private val _recommendations = MutableLiveData<List<MediaItem>>()
    val recommendations: LiveData<List<MediaItem>> = _recommendations

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    var currentUserId: String = ""

    fun loadRecommendations() {
        viewModelScope.launch {
            _isLoading.value = true
            _recommendations.value = repo.getRecommendations(currentUserId)
            _isLoading.value = false
        }
    }
}
