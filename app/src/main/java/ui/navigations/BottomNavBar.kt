package com.example.practice3.ui.navigations

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem

sealed class Screen(val route: String, val label: String) {
    object Movies : Screen("movies", "Movies")
    object Favorites : Screen("favorites", "Favorites")
    object FilterSettings : Screen("filter_settings", "Filter Settings")
    object MovieDetails : Screen("details/{movieId}", "Movie Details")

    companion object {
        fun createMovieDetailsRoute(movieId: Int) = "details/$movieId"
    }
}

@Composable
fun BottomNavBar(selectedScreen: Screen, onScreenSelected: (Screen) -> Unit) {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(androidx.compose.material.icons.Icons.Default.Home, contentDescription = "Movies") },
            label = { Text("Movies") },
            selected = selectedScreen == Screen.Movies,
            onClick = { onScreenSelected(Screen.Movies) }
        )
        NavigationBarItem(
            icon = { Icon(androidx.compose.material.icons.Icons.Default.Favorite, contentDescription = "Favorites") },
            label = { Text("Favorites") },
            selected = selectedScreen == Screen.Favorites,
            onClick = { onScreenSelected(Screen.Favorites) }
        )
    }
}
