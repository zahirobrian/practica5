package com.ipn.practica5.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.ipn.practica5.App
import com.ipn.practica5.data.local.entity.MediaItem
import kotlinx.coroutines.launch

class FavoritesViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = (application as App).repository
    val favorites: LiveData<List<MediaItem>> = repo.getFavorites()

    fun toggleFavorite(item: MediaItem) = viewModelScope.launch {
        repo.toggleFavorite(item)
    }
}
