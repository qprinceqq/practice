package com.example.practice3.di

import android.content.Context
import androidx.room.Room
import com.example.practice3.data.AppDatabase
import com.example.practice3.data.BadgeCache
import com.example.practice3.data.FavoritesRepository
import com.example.practice3.data.FilterSettingsRepository
import com.example.practice3.data.ProfileRepository
import com.example.practice3.network.KinopoiskApi
import com.example.practice3.network.createKinopoiskApi
import com.example.practice3.repository.MovieRepository
import com.example.practice3.usecase.GetMoviesUseCase
import com.example.practice3.viewmodel.FavoritesViewModel
import com.example.practice3.viewmodel.FilterSettingsViewModel
import com.example.practice3.viewmodel.MovieViewModel
import com.example.practice3.viewmodel.ProfileViewModel

/**
 * DI контейнер приложения
 * Управляет зависимостями и их жизненным циклом
 */
object AppContainer {

    private var context: Context? = null

    fun init(context: Context) {
        this.context = context.applicationContext
    }

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

    private val database: AppDatabase by lazy {
        val ctx = requireNotNull(context) { "AppContainer must be initialized with context" }
        Room.databaseBuilder(
            ctx,
            AppDatabase::class.java,
            "movie_app_database"
        ).build()
    }

    private val favoritesRepository: FavoritesRepository by lazy {
        FavoritesRepository(database.favoritesDao())
    }

    val badgeCache: BadgeCache by lazy {
        BadgeCache()
    }

    private val profileRepository: ProfileRepository by lazy {
        val ctx = requireNotNull(context) { "AppContainer must be initialized with context" }
        ProfileRepository(ctx)
    }

    // Domain слой
    val getMoviesUseCase: GetMoviesUseCase by lazy {
        GetMoviesUseCase(movieRepository)
    }

    // ViewModels
    val movieViewModel: MovieViewModel by lazy {
        val ctx = requireNotNull(context) { "AppContainer must be initialized with context" }
        MovieViewModel(ctx, favoritesRepository)
    }

    val favoritesViewModel: FavoritesViewModel by lazy {
        FavoritesViewModel(favoritesRepository)
    }

    // Функция для создания FilterSettingsViewModel с контекстом
    fun createFilterSettingsViewModel(): FilterSettingsViewModel {
        val ctx = requireNotNull(context) { "AppContainer must be initialized with context" }
        val filterSettingsRepository = FilterSettingsRepository(ctx)
        return FilterSettingsViewModel(filterSettingsRepository, badgeCache)
    }

    // Функция для создания ProfileViewModel с контекстом
    fun createProfileViewModel(): ProfileViewModel {
        return ProfileViewModel(profileRepository)
    }
}
