package com.example.practice3.repository

import android.util.Log
import com.example.practice3.model.Movie
import com.example.practice3.network.KinopoiskApi
import com.example.practice3.network.toMovie
import retrofit2.HttpException
import java.io.IOException

class MovieRepository(private val api: KinopoiskApi, private val apiKey: String) {

    suspend fun getMovies(
        rating: String = "8-10",
        limit: Int = 20
    ): Result<List<Movie>> {
        return try {
            Log.d("MovieRepository", "Making API request with rating: $rating, limit: $limit")

            val response = api.getTopMovies(
                apiKey = apiKey,
                page = 1,
                limit = limit,
                rating = rating
            )

            Log.d("MovieRepository", "API response received successfully")

            val movies: List<Movie> = response.docs.map { it.toMovie() }
            Log.d("MovieRepository", "Movies mapped successfully: ${movies.size}")

            Result.success(movies)

        } catch (e: retrofit2.HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Log.e("MovieRepository", "HTTP ${e.code()}: $errorBody")
            Result.failure(Exception("Ошибка сервера: ${e.code()}. $errorBody"))
        } catch (e: java.io.IOException) {
            Log.e("MovieRepository", "Network error: ${e.message}", e)
            Result.failure(Exception("Ошибка сети: проверьте подключение к интернету"))
        } catch (e: com.squareup.moshi.JsonDataException) {
            Log.e("MovieRepository", "JSON parsing error: ${e.message}", e)
            Result.failure(Exception("Ошибка обработки данных от сервера: ${e.message}"))
        } catch (e: Exception) {
            Log.e("MovieRepository", "Unexpected error: ${e.message}", e)
            Result.failure(Exception("Неожиданная ошибка: ${e.message}"))
        }
    }
}
