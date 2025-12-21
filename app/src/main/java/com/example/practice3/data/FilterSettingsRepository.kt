package com.example.practice3.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.practice3.model.MovieFilterSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "filter_settings")

class FilterSettingsRepository(private val context: Context) {

    private val MIN_RATING_KEY = floatPreferencesKey("min_rating")
    private val GENRE_KEY = stringPreferencesKey("genre")
    private val YEAR_KEY = stringPreferencesKey("year")

    val filterSettings: Flow<MovieFilterSettings> = context.dataStore.data
        .map { preferences ->
            MovieFilterSettings(
                minRating = preferences[MIN_RATING_KEY] ?: 0f,
                genre = preferences[GENRE_KEY] ?: "",
                year = preferences[YEAR_KEY] ?: ""
            )
        }

    suspend fun saveFilterSettings(settings: MovieFilterSettings) {
        context.dataStore.edit { preferences ->
            preferences[MIN_RATING_KEY] = settings.minRating
            preferences[GENRE_KEY] = settings.genre
            preferences[YEAR_KEY] = settings.year
        }
    }

    suspend fun resetFilterSettings() {
        saveFilterSettings(MovieFilterSettings.DEFAULT)
    }
}
