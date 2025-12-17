package com.example.practice3

import com.example.practice3.viewmodel.MovieViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class MovieViewModelTest {

    private val viewModel = MovieViewModel()

    @Test
    fun `getMovieById returns correct movie`() {
        val movie = viewModel.getMovieById(1)
        assertNotNull(movie)
        assertEquals(1, movie?.id)
    }
}

