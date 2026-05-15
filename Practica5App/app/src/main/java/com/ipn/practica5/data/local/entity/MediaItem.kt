package com.ipn.practica5.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad Room que representa un libro o serie almacenado localmente.
 * Se usa tanto para resultados de búsqueda (caché offline) como para favoritos.
 */
@Entity(tableName = "media_items")
data class MediaItem(
    @PrimaryKey val id: String,
    val title: String,
    val subtitle: String,       // autor (libro) o género (serie)
    val year: String,
    val coverUrl: String,
    val description: String,
    val type: String,           // "book" o "show"
    val isFavorite: Boolean = false,
    val searchQuery: String = "",
    val cachedAt: Long = System.currentTimeMillis()
)
