package com.example.practice3.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.practice3.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.profileDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_profile")

class ProfileRepository(private val context: Context) {

    private val FULL_NAME_KEY = stringPreferencesKey("full_name")
    private val AVATAR_URI_KEY = stringPreferencesKey("avatar_uri")
    private val RESUME_URL_KEY = stringPreferencesKey("resume_url")
    private val POSITION_KEY = stringPreferencesKey("position")

    val userProfile: Flow<UserProfile> = context.profileDataStore.data
        .map { preferences ->
            UserProfile(
                fullName = preferences[FULL_NAME_KEY] ?: "",
                avatarUri = preferences[AVATAR_URI_KEY],
                resumeUrl = preferences[RESUME_URL_KEY] ?: "",
                position = preferences[POSITION_KEY] ?: ""
            )
        }

    suspend fun saveUserProfile(profile: UserProfile) {
        context.profileDataStore.edit { preferences ->
            preferences[FULL_NAME_KEY] = profile.fullName
            profile.avatarUri?.let { preferences[AVATAR_URI_KEY] = it }
            preferences[RESUME_URL_KEY] = profile.resumeUrl
            preferences[POSITION_KEY] = profile.position
        }
    }

    suspend fun clearUserProfile() {
        context.profileDataStore.edit { preferences ->
            preferences.remove(FULL_NAME_KEY)
            preferences.remove(AVATAR_URI_KEY)
            preferences.remove(RESUME_URL_KEY)
            preferences.remove(POSITION_KEY)
        }
    }
}

