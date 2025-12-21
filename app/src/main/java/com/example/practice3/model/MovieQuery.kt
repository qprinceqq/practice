package com.example.practice3.model

/**
 * Параметры запроса для получения фильмов
 */
data class MovieQuery(
    val rating: String = "8-10",
    val limit: Int = 20,
    val page: Int = 1,
    val genre: String = "",
    val year: String = ""
) {
    companion object {
        val HIGH_RATED = MovieQuery(rating = "9-10", limit = 10)
        val POPULAR = MovieQuery(rating = "8-9", limit = 20)
        val ALL = MovieQuery(rating = "0-10", limit = 50)
    }
}

/**
 * Настройки фильтрации для поиска фильмов
 */
data class MovieFilterSettings(
    val minRating: Float = 0f,
    val genre: String = "",
    val year: String = ""
) {
    companion object {
        val DEFAULT = MovieFilterSettings()
    }

    fun isDefault(): Boolean = this == DEFAULT
}

