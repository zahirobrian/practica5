package com.ipn.practica5.data.remote.api

import com.ipn.practica5.data.remote.model.BookSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenLibraryApi {
    @GET("search.json")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("limit") limit: Int = 20,
        @Query("fields") fields: String = "key,title,author_name,first_publish_year,cover_i,subject"
    ): BookSearchResponse

    @GET("search.json")
    suspend fun searchByAuthor(
        @Query("author") author: String,
        @Query("limit") limit: Int = 20,
        @Query("fields") fields: String = "key,title,author_name,first_publish_year,cover_i,subject"
    ): BookSearchResponse
}
