package com.example.practice3.di

import com.example.practice3.network.KinopoiskApi
import com.example.practice3.network.createKinopoiskApi
import com.example.practice3.repository.MovieRepository
import com.example.practice3.usecase.GetMoviesUseCase
import com.example.practice3.viewmodel.MovieViewModel

/**
 * DI контейнер приложения
 * Управляет зависимостями и их жизненным циклом
 */
object AppContainer {

    // Network слой
    private val kinopoiskApi: KinopoiskApi by lazy {
        createKinopoiskApi()
    }

    // Data слой
    private val movieRepository: MovieRepository by lazy {
        // В реальном приложении API ключ должен читаться из защищенного хранилища
        val apiKey = "R144ZJX-KAGMCR6-QZH3SPD-E7V6NRZ"
        MovieRepository(kinopoiskApi, apiKey)
    }

    // Domain слой
    val getMoviesUseCase: GetMoviesUseCase by lazy {
        GetMoviesUseCase(movieRepository)
    }

    // ViewModel
    val movieViewModel: MovieViewModel by lazy {
        MovieViewModel()
    }
}
