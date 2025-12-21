package com.example.practice3.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_movies")
data class FavoriteMovie(
    @PrimaryKey
    val id: Int,
    val title: String,
    val year: Int,
    val rating: Double,
    val posterUrl: String,
    val description: String,
    val addedAt: Long = System.currentTimeMillis()
)
