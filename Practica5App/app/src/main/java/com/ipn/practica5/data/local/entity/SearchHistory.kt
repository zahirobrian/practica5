package com.ipn.practica5.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad Room para el historial de búsquedas del usuario.
 */
@Entity(tableName = "search_history")
data class SearchHistory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String,
    val query: String,
    val type: String,   // "book" o "show"
    val timestamp: Long = System.currentTimeMillis()
)
