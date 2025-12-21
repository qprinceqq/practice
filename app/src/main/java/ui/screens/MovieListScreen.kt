package com.example.practice3.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.practice3.di.AppContainer
import com.example.practice3.model.Movie
import com.example.practice3.ui.components.Badge
import com.example.practice3.viewmodel.MovieViewModel
import kotlinx.coroutines.launch

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Загрузка фильмов...",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun ErrorScreen(error: String, onRetry: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = "Ошибка загрузки",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRetry) {
                Text("Повторить")
            }
        }
    }
}

@Composable
fun MovieItem(
    movie: Movie,
    onMovieClick: (Int) -> Unit,
    onFavoriteClick: (Movie) -> Unit,
    isFavorite: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onMovieClick(movie.id) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .testTag("movie_item_${movie.id}"),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = movie.posterUrl,
                contentDescription = movie.title,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Год: ${movie.year}",
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
                        text = "${movie.rating}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            IconButton(onClick = { onFavoriteClick(movie) }) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = if (isFavorite) "Удалить из избранного" else "Добавить в избранное",
                    tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun MovieItemWithFavorite(
    movie: Movie,
    viewModel: MovieViewModel,
    onMovieClick: (Int) -> Unit
) {
    val scope = rememberCoroutineScope()
    var isFavorite by remember { mutableStateOf(false) }

    // Проверяем статус избранного при первой загрузке
    LaunchedEffect(movie.id) {
        isFavorite = viewModel.isFavorite(movie.id)
    }

    MovieItem(
        movie = movie,
        onMovieClick = onMovieClick,
        onFavoriteClick = { clickedMovie ->
            scope.launch {
                viewModel.toggleFavorite(clickedMovie)
                isFavorite = !isFavorite
            }
        },
        isFavorite = isFavorite
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieListScreen(
    viewModel: MovieViewModel,
    onMovieClick: (Int) -> Unit,
    onFilterClick: () -> Unit
) {
    val movies by viewModel.movies.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val shouldShowBadge by AppContainer.badgeCache.shouldShowBadge.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Фильмы") },
                actions = {
                    Box {
                        IconButton(onClick = onFilterClick) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Фильтры"
                        )
                        }
                        if (shouldShowBadge) {
                            Badge(
                                modifier = Modifier.align(Alignment.TopEnd)
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
        ) {
        when {
            isLoading && movies.isEmpty() -> {
                // Показываем загрузку только при первом запуске
                LoadingScreen()
            }
            error != null && movies.isEmpty() -> {
                // Показываем ошибку только если нет данных
                ErrorScreen(error = error as String, onRetry = { viewModel.retryLoadMovies() })
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("movies_list"),
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(movies) { movie ->
                        MovieItemWithFavorite(
                            movie = movie,
                            viewModel = viewModel,
                            onMovieClick = onMovieClick
                        )
                    }
                }

                // Показываем индикатор загрузки при обновлении данных
                if (isLoading && movies.isNotEmpty()) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopCenter)
                    )
                }
            }
        }
    }
}

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Загрузка фильмов...",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun ErrorScreen(error: String, onRetry: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = "Ошибка загрузки",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRetry) {
                Text("Повторить")
            }
        }
    }
}

@Composable
fun MovieItem(
    movie: Movie,
    onMovieClick: (Int) -> Unit,
    onFavoriteClick: (Movie) -> Unit,
    isFavorite: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onMovieClick(movie.id) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .testTag("movie_item_${movie.id}"),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = movie.posterUrl,
                contentDescription = movie.title,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Год: ${movie.year}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    androidx.compose.material3.Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Rating",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${movie.rating}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            IconButton(onClick = { onFavoriteClick(movie) }) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = if (isFavorite) "Удалить из избранного" else "Добавить в избранное",
                    tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        }
    }
}
