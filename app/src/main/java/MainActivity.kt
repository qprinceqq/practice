package com.example.practice3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.practice3.di.AppContainer
import com.example.practice3.ui.navigations.BottomNavBar
import com.example.practice3.ui.navigations.NavGraph
import com.example.practice3.ui.navigations.Screen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppContainer.init(this)
        setContent {
            val navController = rememberNavController()

            Scaffold(
                bottomBar = {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route

                    // Показываем bottom bar только на основных экранах
                    if (currentRoute == Screen.Movies.route || currentRoute == Screen.Favorites.route) {
                        val selectedScreen = when (currentRoute) {
                            Screen.Movies.route -> Screen.Movies
                            Screen.Favorites.route -> Screen.Favorites
                            else -> Screen.Movies
                        }

                        BottomNavBar(selectedScreen = selectedScreen) { screen ->
                            navController.navigate(screen.route) {
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                }
            ) { padding ->
                Box(modifier = Modifier.padding(padding)) {
                    NavGraph(navController = navController)
                }
            }
        }
    }
}

