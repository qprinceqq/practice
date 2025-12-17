package com.example.practice3.ui.navigations


import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.practice3.ui.screens.FavoritesScreen
import com.example.practice3.ui.screens.MovieDetailScreen
import com.example.practice3.ui.screens.MovieListScreen
import com.example.practice3.viewmodel.MovieViewModel

@Composable
fun NavGraph(navController: NavHostController) {
    val movieViewModel: MovieViewModel = viewModel()

    NavHost(navController = navController, startDestination = "movies") {
        composable("movies") {
            MovieListScreen(
                movies = movieViewModel.movies,
                onMovieClick = { movieId ->
                    navController.navigate("details/$movieId")
                }
            )
        }
        composable("details/{movieId}") { backStackEntry ->
            val movieId = backStackEntry.arguments?.getString("movieId")?.toInt() ?: 0
            val movie = movieViewModel.getMovieById(movieId)
            movie?.let { MovieDetailScreen(it) }
        }
        composable("favorites") {
            FavoritesScreen()
        }
    }
}
