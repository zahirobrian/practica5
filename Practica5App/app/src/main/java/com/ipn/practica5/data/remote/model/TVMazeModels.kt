package com.ipn.practica5.data.remote.model

import com.google.gson.annotations.SerializedName

// ── TVMaze API models ──────────────────────────────────────────────────

data class ShowSearchResult(
    @SerializedName("score") val score: Double = 0.0,
    @SerializedName("show") val show: Show
)

data class Show(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("name") val name: String = "",
    @SerializedName("type") val type: String = "",
    @SerializedName("language") val language: String? = null,
    @SerializedName("genres") val genres: List<String> = emptyList(),
    @SerializedName("status") val status: String = "",
    @SerializedName("premiered") val premiered: String? = null,
    @SerializedName("rating") val rating: Rating? = null,
    @SerializedName("image") val image: ShowImage? = null,
    @SerializedName("summary") val summary: String? = null,
    @SerializedName("network") val network: Network? = null
) {
    val coverUrl: String get() = image?.medium ?: image?.original ?: ""
    val genreDisplay: String get() = genres.take(2).joinToString(", ").ifBlank { "Sin género" }
    val yearDisplay: String get() = premiered?.take(4) ?: "—"
    val cleanSummary: String get() = summary?.replace(Regex("<[^>]*>"), "") ?: "Sin descripción"
    val idStr: String get() = "tvmaze_$id"
}

data class Rating(@SerializedName("average") val average: Double? = null)
data class ShowImage(
    @SerializedName("medium") val medium: String? = null,
    @SerializedName("original") val original: String? = null
)
data class Network(@SerializedName("name") val name: String? = null)
