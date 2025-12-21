package com.example.practice3.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.practice3.di.AppContainer
import com.example.practice3.model.FavoriteMovie
import com.example.practice3.viewmodel.FavoritesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    onMovieClick: (Int) -> Unit = {}
) {
    val viewModel = remember { AppContainer.favoritesViewModel }
    val favoriteMovies by viewModel.favoriteMovies.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Избранное") }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
        ) {
            if (favoriteMovies.isEmpty()) {
                EmptyFavoritesScreen()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(favoriteMovies) { favoriteMovie ->
                        FavoriteMovieItem(
                            favoriteMovie = favoriteMovie,
                            onMovieClick = onMovieClick,
                            onRemoveClick = { favoriteToRemove ->
                                viewModel.toggleFavorite(
                                    com.example.practice3.model.Movie(
                                        id = favoriteToRemove.id,
                                        title = favoriteToRemove.title,
                                        year = favoriteToRemove.year,
                                        rating = favoriteToRemove.rating,
                                        posterUrl = favoriteToRemove.posterUrl,
                                        description = favoriteToRemove.description
                                    )
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyFavoritesScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "У вас пока нет избранных фильмов",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Добавьте фильмы в избранное, нажав на сердечко",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun FavoriteMovieItem(
    favoriteMovie: FavoriteMovie,
    onMovieClick: (Int) -> Unit,
    onRemoveClick: (FavoriteMovie) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onMovieClick(favoriteMovie.id) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = favoriteMovie.posterUrl,
                contentDescription = favoriteMovie.title,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = favoriteMovie.title,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Год: ${favoriteMovie.year}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Rating",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${favoriteMovie.rating}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            IconButton(onClick = { onRemoveClick(favoriteMovie) }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Удалить из избранного",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
