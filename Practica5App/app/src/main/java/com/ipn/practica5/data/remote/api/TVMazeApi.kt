package com.ipn.practica5.data.remote.api

import com.ipn.practica5.data.remote.model.ShowSearchResult
import retrofit2.http.GET
import retrofit2.http.Query

interface TVMazeApi {
    @GET("search/shows")
    suspend fun searchShows(@Query("q") query: String): List<ShowSearchResult>
}
