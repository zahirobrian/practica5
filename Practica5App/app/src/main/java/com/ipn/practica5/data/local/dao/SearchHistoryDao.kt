package com.ipn.practica5.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ipn.practica5.data.local.entity.SearchHistory

@Dao
interface SearchHistoryDao {

    @Query("SELECT * FROM search_history WHERE userId = :userId ORDER BY timestamp DESC LIMIT 20")
    fun getHistory(userId: String): LiveData<List<SearchHistory>>

    @Query("SELECT * FROM search_history WHERE userId = :userId ORDER BY timestamp DESC LIMIT 20")
    suspend fun getHistoryList(userId: String): List<SearchHistory>

    @Insert
    suspend fun insert(history: SearchHistory)

    @Query("DELETE FROM search_history WHERE userId = :userId")
    suspend fun clearHistory(userId: String)

    @Query("SELECT DISTINCT query FROM search_history WHERE userId = :userId ORDER BY timestamp DESC LIMIT 10")
    suspend fun getRecentQueries(userId: String): List<String>
}
