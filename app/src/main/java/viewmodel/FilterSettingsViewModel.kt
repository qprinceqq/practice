package com.example.practice3.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.practice3.data.BadgeCache
import com.example.practice3.data.FilterSettingsRepository
import com.example.practice3.model.MovieFilterSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FilterSettingsViewModel(
    private val filterSettingsRepository: FilterSettingsRepository,
    private val badgeCache: BadgeCache
) : ViewModel() {

    private val _filterSettings = MutableStateFlow(MovieFilterSettings.DEFAULT)
    val filterSettings: StateFlow<MovieFilterSettings> = _filterSettings.asStateFlow()

    init {
        viewModelScope.launch {
            filterSettingsRepository.filterSettings.collect { settings ->
                _filterSettings.value = settings
                badgeCache.updateBadgeState(settings)
            }
        }
    }

    fun updateMinRating(rating: Float) {
        val currentSettings = _filterSettings.value
        _filterSettings.value = currentSettings.copy(minRating = rating)
    }

    fun updateGenre(genre: String) {
        val currentSettings = _filterSettings.value
        _filterSettings.value = currentSettings.copy(genre = genre)
    }

    fun updateYear(year: String) {
        val currentSettings = _filterSettings.value
        _filterSettings.value = currentSettings.copy(year = year)
    }

    fun saveSettings() {
        viewModelScope.launch {
            filterSettingsRepository.saveFilterSettings(_filterSettings.value)
            badgeCache.updateBadgeState(_filterSettings.value)
        }
    }

    fun resetSettings() {
        viewModelScope.launch {
            filterSettingsRepository.resetFilterSettings()
            _filterSettings.value = MovieFilterSettings.DEFAULT
            badgeCache.updateBadgeState(MovieFilterSettings.DEFAULT)
        }
    }
}
