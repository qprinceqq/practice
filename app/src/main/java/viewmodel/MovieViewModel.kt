package com.example.practice3.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import com.example.practice3.model.Movie
import com.example.practice3.model.MovieQuery
import com.example.practice3.model.MovieFilterSettings
import com.example.practice3.repository.MovieRepository
import com.example.practice3.data.FilterSettingsRepository
import com.example.practice3.data.FavoritesRepository
import com.example.practice3.network.createKinopoiskApi
import kotlinx.coroutines.launch

class MovieViewModel(
    context: Context,
    private val favoritesRepository: FavoritesRepository
) : ViewModel() {

    private val repository: MovieRepository = run {
        val apiKey = "R144ZJX-KAGMCR6-QZH3SPD-E7V6NRZ"
        val api = createKinopoiskApi()
        MovieRepository(api, apiKey)
    }

    private val filterSettingsRepository = FilterSettingsRepository(context)

    private val _movies = MutableStateFlow<List<Movie>>(emptyList())
    val movies: StateFlow<List<Movie>> = _movies

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    val filterSettings = filterSettingsRepository.filterSettings

    init {
        viewModelScope.launch {
            filterSettingsRepository.filterSettings.collect { settings ->
                loadMoviesWithFilters(settings)
            }
        }
    }

    private fun loadMoviesWithFilters(settings: MovieFilterSettings) {
        _isLoading.value = true
        _error.value = null

        val rating = if (settings.minRating > 0f) {
            "${settings.minRating}-${10f}"
        } else {
            "0-10"
        }

        val query = MovieQuery(
            rating = rating,
            genre = settings.genre,
            year = settings.year
        )

        viewModelScope.launch {
            repository.getMovies(query.rating, query.limit)
                .onSuccess { movieList ->
                    // Примитивная фильтрация на клиенте (в реальном приложении фильтрация должна быть на сервере)
                    val filteredMovies = movieList.filter { movie ->
                        val genreMatch = settings.genre.isBlank() ||
                                movie.title.contains(settings.genre, ignoreCase = true) // упрощенная проверка жанра
                        val yearMatch = settings.year.isBlank() ||
                                movie.year.toString() == settings.year
                        genreMatch && yearMatch
                    }
                    _movies.value = filteredMovies
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "Неизвестная ошибка"
                }
            _isLoading.value = false
        }
    }

    fun loadMovies(query: MovieQuery = MovieQuery()) {
        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            repository.getMovies(query.rating, query.limit)
                .onSuccess { movieList ->
                    _movies.value = movieList
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "Неизвестная ошибка"
                }
            _isLoading.value = false
        }
    }

    fun getMovieById(id: Int): Movie? {
        return movies.value.find { it.id == id }
    }

    fun retryLoadMovies() {
        loadMovies()
    }

    fun loadHighRatedMovies() {
        loadMovies(MovieQuery.HIGH_RATED)
    }

    fun loadPopularMovies() {
        loadMovies(MovieQuery.POPULAR)
    }

    fun loadAllMovies() {
        loadMovies(MovieQuery.ALL)
    }

    fun toggleFavorite(movie: Movie) {
        viewModelScope.launch {
            favoritesRepository.toggleFavorite(movie)
        }
    }

    suspend fun isFavorite(movieId: Int): Boolean {
        return favoritesRepository.isFavorite(movieId)
    }
}
