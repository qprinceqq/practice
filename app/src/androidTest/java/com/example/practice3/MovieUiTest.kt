package com.example.practice3

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test

class MovieUiTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun moviesList_showsMockMovies() {
        // Проверяем, что список отображается
        composeTestRule
            .onNodeWithTag("movies_list")
            .assertIsDisplayed()

        // Проверяем, что как минимум один фильм из MockData виден
        composeTestRule
            .onNodeWithText("Inception")
            .assertIsDisplayed()
    }
}

