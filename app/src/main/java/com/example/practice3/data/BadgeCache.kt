package com.example.practice3.data

import com.example.practice3.model.MovieFilterSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Кэш для управления состоянием бейджа на экранах
 * Хранит информацию о том, нужно ли показывать бейдж (индикатор настроек в дефолтном состоянии)
 */
class BadgeCache {

    private val _shouldShowBadge = MutableStateFlow(false)
    val shouldShowBadge: StateFlow<Boolean> = _shouldShowBadge.asStateFlow()

    /**
     * Обновляет состояние бейджа на основе текущих настроек фильтрации
     */
    fun updateBadgeState(filterSettings: MovieFilterSettings) {
        _shouldShowBadge.value = !filterSettings.isDefault()
    }

    /**
     * Сбрасывает состояние бейджа
     */
    fun resetBadge() {
        _shouldShowBadge.value = false
    }
}
