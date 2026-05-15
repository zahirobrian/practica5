package com.ipn.practica5.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.LiveData
import com.ipn.practica5.data.local.AppDatabase
import com.ipn.practica5.data.local.entity.MediaItem
import com.ipn.practica5.data.local.entity.SearchHistory
import com.ipn.practica5.data.remote.api.RetrofitClient

/**
 * Repositorio central que gestiona datos de APIs remotas y base de datos local.
 *
 * Estrategia de sincronización:
 * 1. Si hay red → consulta API → guarda en Room → devuelve resultados
 * 2. Sin red → devuelve datos cacheados de Room (modo offline)
 */
class MediaRepository(private val context: Context) {

    private val db = AppDatabase.getInstance(context)
    private val mediaDao = db.mediaItemDao()
    private val historyDao = db.searchHistoryDao()

    // ── Conectividad ──────────────────────────────────────────────────────

    fun isOnline(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val net = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(net) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    // ── Búsqueda de Libros (Open Library) ────────────────────────────────

    suspend fun searchBooks(query: String, userId: String): List<MediaItem> {
        // Guarda en historial
        historyDao.insert(SearchHistory(userId = userId, query = query, type = "book"))

        return if (isOnline()) {
            try {
                val response = RetrofitClient.openLibraryApi.searchBooks(query)
                val items = response.docs.take(20).map { doc ->
                    MediaItem(
                        id = "book_${doc.id}",
                        title = doc.title,
                        subtitle = doc.authorDisplay,
                        year = doc.yearDisplay,
                        coverUrl = doc.coverUrl,
                        description = doc.subjects?.take(3)?.joinToString(", ") ?: "",
                        type = "book",
                        searchQuery = query
                    )
                }
                // Cachea en Room (sincronización local)
                if (items.isNotEmpty()) {
                    mediaDao.insertAll(items)
                }
                items
            } catch (e: Exception) {
                // Fallo de red → devuelve caché
                mediaDao.getCachedSearch(query, "book")
            }
        } else {
            // Sin conexión → modo offline con datos locales
            mediaDao.getCachedSearch(query, "book")
        }
    }

    // ── Búsqueda de Series (TVMaze) ───────────────────────────────────────

    suspend fun searchShows(query: String, userId: String): List<MediaItem> {
        historyDao.insert(SearchHistory(userId = userId, query = query, type = "show"))

        return if (isOnline()) {
            try {
                val results = RetrofitClient.tvMazeApi.searchShows(query)
                val items = results.map { result ->
                    val show = result.show
                    MediaItem(
                        id = show.idStr,
                        title = show.name,
                        subtitle = show.genreDisplay,
                        year = show.yearDisplay,
                        coverUrl = show.coverUrl,
                        description = show.cleanSummary,
                        type = "show",
                        searchQuery = query
                    )
                }
                if (items.isNotEmpty()) mediaDao.insertAll(items)
                items
            } catch (e: Exception) {
                mediaDao.getCachedSearch(query, "show")
            }
        } else {
            mediaDao.getCachedSearch(query, "show")
        }
    }

    // ── Favoritos ─────────────────────────────────────────────────────────

    fun getFavorites(): LiveData<List<MediaItem>> = mediaDao.getFavorites()

    suspend fun toggleFavorite(item: MediaItem) {
        val existing = mediaDao.getById(item.id)
        if (existing == null) {
            mediaDao.insert(item.copy(isFavorite = true))
        } else {
            mediaDao.updateFavorite(item.id, !existing.isFavorite)
        }
    }

    suspend fun isFavorite(id: String): Boolean =
        mediaDao.getById(id)?.isFavorite ?: false

    // ── Recomendaciones ───────────────────────────────────────────────────

    /**
     * Genera recomendaciones basadas en:
     * 1. Los favoritos del usuario (mismo tipo y género/autor)
     * 2. Historial de búsquedas recientes
     */
    suspend fun getRecommendations(userId: String): List<MediaItem> {
        val recommendations = mutableListOf<MediaItem>()

        if (!isOnline()) {
            // Offline: devuelve favoritos como recomendaciones
            return mediaDao.getFavoritesList()
        }

        // Basado en favoritos de libros
        val bookFavs = mediaDao.getFavoritesByType("book")
        if (bookFavs.isNotEmpty()) {
            val author = bookFavs.firstOrNull()?.subtitle ?: ""
            if (author.isNotBlank()) {
                try {
                    val resp = RetrofitClient.openLibraryApi.searchByAuthor(author, 6)
                    val items = resp.docs.take(6).map { doc ->
                        MediaItem(
                            id = "book_${doc.id}",
                            title = doc.title,
                            subtitle = doc.authorDisplay,
                            year = doc.yearDisplay,
                            coverUrl = doc.coverUrl,
                            description = doc.subjects?.take(3)?.joinToString(", ") ?: "",
                            type = "book",
                            searchQuery = "rec_$author"
                        )
                    }
                    mediaDao.insertAll(items)
                    recommendations.addAll(items)
                } catch (e: Exception) { /* ignora */ }
            }
        }

        // Basado en favoritos de series
        val showFavs = mediaDao.getFavoritesByType("show")
        if (showFavs.isNotEmpty()) {
            val genre = showFavs.firstOrNull()?.subtitle?.split(",")?.firstOrNull()?.trim() ?: ""
            if (genre.isNotBlank()) {
                try {
                    val results = RetrofitClient.tvMazeApi.searchShows(genre)
                    val items = results.take(6).map { result ->
                        val show = result.show
                        MediaItem(
                            id = show.idStr,
                            title = show.name,
                            subtitle = show.genreDisplay,
                            year = show.yearDisplay,
                            coverUrl = show.coverUrl,
                            description = show.cleanSummary,
                            type = "show",
                            searchQuery = "rec_$genre"
                        )
                    }
                    mediaDao.insertAll(items)
                    recommendations.addAll(items)
                } catch (e: Exception) { /* ignora */ }
            }
        }

        // Si no hay favoritos, usa historial
        if (recommendations.isEmpty()) {
            val recentQueries = historyDao.getRecentQueries(userId)
            recentQueries.firstOrNull()?.let { q ->
                try {
                    val results = RetrofitClient.tvMazeApi.searchShows(q)
                    results.take(6).forEach { result ->
                        val show = result.show
                        recommendations.add(MediaItem(
                            id = show.idStr, title = show.name,
                            subtitle = show.genreDisplay, year = show.yearDisplay,
                            coverUrl = show.coverUrl, description = show.cleanSummary,
                            type = "show", searchQuery = "rec_$q"
                        ))
                    }
                } catch (e: Exception) { /* ignora */ }
            }
        }

        return recommendations.distinctBy { it.id }
    }

    // ── Historial ─────────────────────────────────────────────────────────

    fun getSearchHistory(userId: String): LiveData<List<SearchHistory>> =
        historyDao.getHistory(userId)

    suspend fun clearHistory(userId: String) = historyDao.clearHistory(userId)

    // ── Limpieza de caché ─────────────────────────────────────────────────

    suspend fun cleanOldCache() {
        val oneDayAgo = System.currentTimeMillis() - 24 * 60 * 60 * 1000
        mediaDao.deleteOldCache(oneDayAgo)
    }
}
