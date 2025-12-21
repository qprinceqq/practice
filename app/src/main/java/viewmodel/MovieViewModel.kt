package com.example.practice3.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.practice3.model.Movie
import com.example.practice3.model.MovieQuery
import com.example.practice3.repository.MovieRepository
import com.example.practice3.network.createKinopoiskApi
import kotlinx.coroutines.launch

class MovieViewModel : ViewModel() {

    private val repository: MovieRepository = run {
        val apiKey = "R144ZJX-KAGMCR6-QZH3SPD-E7V6NRZ"
        val api = createKinopoiskApi()
        MovieRepository(api, apiKey)
    }

    private val _movies = MutableStateFlow<List<Movie>>(emptyList())
    val movies: StateFlow<List<Movie>> = _movies

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        loadMovies()
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
}
