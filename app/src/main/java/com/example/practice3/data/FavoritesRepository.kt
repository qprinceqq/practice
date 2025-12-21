package com.example.practice3.data

import com.example.practice3.model.FavoriteMovie
import com.example.practice3.model.Movie
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoritesRepository(private val favoritesDao: FavoritesDao) {

    val favoriteMovies: Flow<List<FavoriteMovie>> = favoritesDao.getAllFavorites()

    suspend fun isFavorite(movieId: Int): Boolean {
        return favoritesDao.isFavorite(movieId) > 0
    }

    suspend fun addToFavorites(movie: Movie) {
        val favoriteMovie = FavoriteMovie(
            id = movie.id,
            title = movie.title,
            year = movie.year,
            rating = movie.rating,
            posterUrl = movie.posterUrl,
            description = movie.description
        )
        favoritesDao.addToFavorites(favoriteMovie)
    }

    suspend fun removeFromFavorites(movieId: Int) {
        favoritesDao.removeFromFavoritesById(movieId)
    }

    suspend fun toggleFavorite(movie: Movie) {
        if (isFavorite(movie.id)) {
            removeFromFavorites(movie.id)
        } else {
            addToFavorites(movie)
        }
    }
}
