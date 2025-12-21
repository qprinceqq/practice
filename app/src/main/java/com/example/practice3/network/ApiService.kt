package com.example.practice3.network

import com.example.practice3.model.Movie
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

private const val BASE_URL = "https://api.kinopoisk.dev/"

data class MovieResponse(
    val docs: List<MovieDto>,
    val total: Int?,
    val limit: Int?,
    val page: Int?,
    val pages: Int?
)

data class MovieDto(
    val id: Int,
    val name: String?,
    val alternativeName: String?,
    val year: Int?,
    val description: String?,
    val rating: RatingDto?,
    val poster: PosterDto?,
    val genres: List<GenreDto>?,
    val countries: List<CountryDto>?
)

data class RatingDto(
    val kp: Double?,
    val imdb: Double?,
    val filmCritics: Double?,
    val russianFilmCritics: Double?,
    val await: Double?
)

data class PosterDto(val url: String?)
data class GenreDto(val name: String?)
data class CountryDto(val name: String?)

fun MovieDto.toMovie(): Movie {
    return Movie(
        id = id,
        title = name ?: alternativeName ?: "Без названия",
        year = year ?: 0,
        rating = rating?.kp ?: rating?.imdb ?: 0.0,
        posterUrl = poster?.url ?: "",
        description = description ?: "Описание отсутствует"
    )
}

interface KinopoiskApi {
    @GET("v1.4/movie")
    suspend fun getTopMovies(
        @Header("X-API-KEY") apiKey: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
        @Query("rating.kp") rating: String = "8-10"
    ): MovieResponse
}

fun createKinopoiskApi(): KinopoiskApi {
    val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    return Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
        .create(KinopoiskApi::class.java)
}
