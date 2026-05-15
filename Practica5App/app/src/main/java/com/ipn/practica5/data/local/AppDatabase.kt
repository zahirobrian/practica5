package com.ipn.practica5.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ipn.practica5.data.local.dao.MediaItemDao
import com.ipn.practica5.data.local.dao.SearchHistoryDao
import com.ipn.practica5.data.local.entity.MediaItem
import com.ipn.practica5.data.local.entity.SearchHistory

@Database(
    entities = [MediaItem::class, SearchHistory::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun mediaItemDao(): MediaItemDao
    abstract fun searchHistoryDao(): SearchHistoryDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null
        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "practica5_db")
                    .build().also { INSTANCE = it }
            }
    }
}
