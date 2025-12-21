package com.example.practice3.data

import androidx.room.*
import com.example.practice3.model.FavoriteMovie
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritesDao {

    @Query("SELECT * FROM favorite_movies ORDER BY addedAt DESC")
    fun getAllFavorites(): Flow<List<FavoriteMovie>>

    @Query("SELECT * FROM favorite_movies WHERE id = :movieId")
    suspend fun getFavoriteById(movieId: Int): FavoriteMovie?

    @Query("SELECT COUNT(*) FROM favorite_movies WHERE id = :movieId")
    suspend fun isFavorite(movieId: Int): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToFavorites(favoriteMovie: FavoriteMovie)

    @Delete
    suspend fun removeFromFavorites(favoriteMovie: FavoriteMovie)

    @Query("DELETE FROM favorite_movies WHERE id = :movieId")
    suspend fun removeFromFavoritesById(movieId: Int)
}
