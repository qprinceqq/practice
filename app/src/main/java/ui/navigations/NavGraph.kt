package com.example.practice3.ui.navigations

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.practice3.di.AppContainer
import com.example.practice3.ui.screens.FavoritesScreen
import com.example.practice3.ui.screens.FilterSettingsScreen
import com.example.practice3.ui.screens.MovieDetailScreen
import com.example.practice3.ui.screens.MovieListScreen
import com.example.practice3.ui.screens.ProfileScreen
import com.example.practice3.ui.screens.EditProfileScreen
import com.example.practice3.viewmodel.MovieViewModel

@Composable
fun NavGraph(navController: NavHostController) {
    val movieViewModel: MovieViewModel = AppContainer.movieViewModel

    NavHost(navController = navController, startDestination = Screen.Movies.route) {
        composable(Screen.Movies.route) {
            MovieListScreen(
                viewModel = movieViewModel,
                onMovieClick = { movieId ->
                    navController.navigate(Screen.createMovieDetailsRoute(movieId))
                },
                onFilterClick = {
                    navController.navigate(Screen.FilterSettings.route)
                }
            )
        }
        composable(
            Screen.MovieDetails.route,
            arguments = listOf(navArgument("movieId") { type = NavType.IntType })
        ) { backStackEntry ->
            val movieId = backStackEntry.arguments?.getInt("movieId") ?: 0
            val movie = movieViewModel.getMovieById(movieId)
            movie?.let { MovieDetailScreen(it) }
        }
        composable(Screen.Favorites.route) {
            FavoritesScreen(
                onMovieClick = { movieId ->
                    navController.navigate(Screen.createMovieDetailsRoute(movieId))
                }
            )
        }
        composable(Screen.FilterSettings.route) {
            FilterSettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onApplyFilters = {
                    navController.popBackStack()
                    // Здесь можно добавить логику обновления списка фильмов
                }
            )
        }
        composable(Screen.Profile.route) {
            ProfileScreen(
                onEditProfileClick = {
                    navController.navigate(Screen.EditProfile.route)
                }
            )
        }
        composable(Screen.EditProfile.route) {
            EditProfileScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
