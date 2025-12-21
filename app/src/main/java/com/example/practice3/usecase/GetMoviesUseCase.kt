package com.example.practice3.usecase

import com.example.practice3.model.Movie
import com.example.practice3.model.MovieQuery
import com.example.practice3.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * UseCase для получения списка фильмов
 * Инкапсулирует бизнес-логику получения фильмов
 */
class GetMoviesUseCase(private val repository: MovieRepository) {

    /**
     * Получить список фильмов
     * @param query Параметры запроса фильмов
     * @return Flow с результатом операции
     */
    operator fun invoke(query: MovieQuery = MovieQuery()): Flow<Result<List<Movie>>> = flow {
        try {
            val result = repository.getMovies(query.rating, query.limit)
            emit(result)
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    /**
     * Получить высокорейтинговые фильмы
     */
    fun getHighRatedMovies(): Flow<Result<List<Movie>>> = invoke(MovieQuery.HIGH_RATED)

    /**
     * Получить популярные фильмы
     */
    fun getPopularMovies(): Flow<Result<List<Movie>>> = invoke(MovieQuery.POPULAR)

    /**
     * Получить все фильмы
     */
    fun getAllMovies(): Flow<Result<List<Movie>>> = invoke(MovieQuery.ALL)
}
