package com.example.practice3.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.Alignment as ComposeAlignment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.practice3.di.AppContainer
import com.example.practice3.ui.components.Badge
import com.example.practice3.viewmodel.FilterSettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterSettingsScreen(
    onNavigateBack: () -> Unit,
    onApplyFilters: () -> Unit
) {
    val viewModel = remember { AppContainer.createFilterSettingsViewModel() }

    val filterSettings by viewModel.filterSettings.collectAsStateWithLifecycle()
    val shouldShowBadge by AppContainer.badgeCache.shouldShowBadge.collectAsStateWithLifecycle()

    var minRating by remember { mutableFloatStateOf(filterSettings.minRating) }
    var genre by remember { mutableStateOf(filterSettings.genre) }
    var year by remember { mutableStateOf(filterSettings.year) }

    // Обновляем локальные состояния при изменении настроек
    LaunchedEffect(filterSettings) {
        minRating = filterSettings.minRating
        genre = filterSettings.genre
        year = filterSettings.year
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box {
                        Text("Настройки поиска")
                        if (shouldShowBadge) {
                            Badge(
                                modifier = Modifier.align(ComposeAlignment.TopEnd)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Text("←")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Минимальный рейтинг
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Минимальный рейтинг",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Текущий: ${minRating.format(1)}")
                    Slider(
                        value = minRating,
                        onValueChange = {
                            minRating = it
                            viewModel.updateMinRating(it)
                        },
                        valueRange = 0f..10f,
                        steps = 19,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "0.0", style = MaterialTheme.typography.bodySmall)
                        Text(text = "10.0", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            // Жанр
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Жанр",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = genre,
                        onValueChange = {
                            genre = it
                            viewModel.updateGenre(it)
                        },
                        label = { Text("Введите жанр") },
                        placeholder = { Text("Например: драма, комедия") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }

            // Год выпуска
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Год выпуска",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = year,
                        onValueChange = {
                            year = it
                            viewModel.updateYear(it)
                        },
                        label = { Text("Введите год") },
                        placeholder = { Text("Например: 2023") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Кнопки действий
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        viewModel.resetSettings()
                        minRating = 0f
                        genre = ""
                        year = ""
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Сбросить")
                }

                Button(
                    onClick = {
                        viewModel.saveSettings()
                        onApplyFilters()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Готово")
                }
            }
        }
    }
}

private fun Float.format(digits: Int): String = "%.${digits}f".format(this)
