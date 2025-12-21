package com.example.practice3.network

import android.content.Context
import java.io.File
import java.util.Properties

object ConfigManager {
    private const val CONFIG_FILE_NAME = "config.env"
    private var apiKey: String? = null

    fun getApiKey(context: android.content.Context): String {
        if (apiKey != null) return apiKey!!

        try {
            // Сначала пытаемся прочитать из assets
            val properties = java.util.Properties()
            context.assets.open(CONFIG_FILE_NAME).use { inputStream ->
                properties.load(inputStream)
            }
            val key = properties.getProperty("KINOPOISK_API_KEY")
            if (!key.isNullOrBlank()) {
                apiKey = key
                return key
            }
        } catch (e: Exception) {
            // Если не удалось прочитать из assets, используем дефолтное значение
        }

        // Fallback - жестко закодированный ключ (только для разработки!)
        apiKey = "R144ZJX-KAGMCR6-QZH3SPD-E7V6NRZ"
        return apiKey!!
    }
}
