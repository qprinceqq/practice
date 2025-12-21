package com.example.practice3

import androidx.room.Room
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.practice3.data.FavoritesRepository
import com.example.practice3.data.AppDatabase
import com.example.practice3.viewmodel.MovieViewModel
import org.junit.Assert.assertNotNull
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MovieViewModelTest {

    private lateinit var context: Context
    private lateinit var database: AppDatabase
    private lateinit var favoritesRepository: FavoritesRepository
    private lateinit var viewModel: MovieViewModel

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        favoritesRepository = FavoritesRepository(database.favoritesDao())
        viewModel = MovieViewModel(context, favoritesRepository)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `viewModel is initialized correctly`() {
        assertNotNull(viewModel)
    }
}

