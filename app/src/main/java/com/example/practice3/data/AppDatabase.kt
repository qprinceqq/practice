package com.example.practice3.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.practice3.model.FavoriteMovie

@Database(
    entities = [FavoriteMovie::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoritesDao(): FavoritesDao
}
