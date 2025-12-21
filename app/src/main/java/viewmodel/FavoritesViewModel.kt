package com.example.practice3.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.practice3.data.FavoritesRepository
import com.example.practice3.model.FavoriteMovie
import com.example.practice3.model.Movie
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val favoritesRepository: FavoritesRepository
) : ViewModel() {

    val favoriteMovies: StateFlow<List<FavoriteMovie>> = favoritesRepository.favoriteMovies
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun toggleFavorite(movie: Movie) {
        viewModelScope.launch {
            favoritesRepository.toggleFavorite(movie)
        }
    }

    suspend fun isFavorite(movieId: Int): Boolean {
        return favoritesRepository.isFavorite(movieId)
    }
}
