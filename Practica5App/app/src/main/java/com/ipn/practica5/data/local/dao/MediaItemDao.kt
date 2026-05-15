package com.ipn.practica5.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ipn.practica5.data.local.entity.MediaItem

@Dao
interface MediaItemDao {

    // Favoritos
    @Query("SELECT * FROM media_items WHERE isFavorite = 1 ORDER BY cachedAt DESC")
    fun getFavorites(): LiveData<List<MediaItem>>

    @Query("SELECT * FROM media_items WHERE isFavorite = 1")
    suspend fun getFavoritesList(): List<MediaItem>

    @Query("SELECT * FROM media_items WHERE id = :id")
    suspend fun getById(id: String): MediaItem?

    // Caché de búsquedas (para modo offline)
    @Query("SELECT * FROM media_items WHERE searchQuery = :query AND type = :type ORDER BY cachedAt DESC")
    suspend fun getCachedSearch(query: String, type: String): List<MediaItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<MediaItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: MediaItem)

    @Query("UPDATE media_items SET isFavorite = :isFav WHERE id = :id")
    suspend fun updateFavorite(id: String, isFav: Boolean)

    @Query("DELETE FROM media_items WHERE isFavorite = 0 AND cachedAt < :before")
    suspend fun deleteOldCache(before: Long)

    @Query("SELECT * FROM media_items WHERE type = :type AND isFavorite = 1")
    suspend fun getFavoritesByType(type: String): List<MediaItem>
}
