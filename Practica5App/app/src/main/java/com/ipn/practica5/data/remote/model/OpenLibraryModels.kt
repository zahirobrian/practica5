package com.ipn.practica5.data.remote.model

import com.google.gson.annotations.SerializedName

// ── Open Library API models ────────────────────────────────────────────

data class BookSearchResponse(
    @SerializedName("docs") val docs: List<BookDoc> = emptyList(),
    @SerializedName("numFound") val numFound: Int = 0
)

data class BookDoc(
    @SerializedName("key") val key: String = "",
    @SerializedName("title") val title: String = "",
    @SerializedName("author_name") val authorName: List<String>? = null,
    @SerializedName("first_publish_year") val firstPublishYear: Int? = null,
    @SerializedName("cover_i") val coverId: Int? = null,
    @SerializedName("subject") val subjects: List<String>? = null,
    @SerializedName("language") val languages: List<String>? = null
) {
    val coverUrl: String get() =
        if (coverId != null) "https://covers.openlibrary.org/b/id/$coverId-M.jpg"
        else ""

    val authorDisplay: String get() = authorName?.firstOrNull() ?: "Autor desconocido"
    val yearDisplay: String get() = firstPublishYear?.toString() ?: "—"
    val id: String get() = key.removePrefix("/works/")
}
