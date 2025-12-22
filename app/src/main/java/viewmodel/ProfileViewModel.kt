package com.example.practice3.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.practice3.data.ProfileRepository
import com.example.practice3.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    init {
        viewModelScope.launch {
            profileRepository.userProfile.collect { profile ->
                _userProfile.value = profile
            }
        }
    }

    fun updateFullName(fullName: String) {
        val currentProfile = _userProfile.value
        _userProfile.value = currentProfile.copy(fullName = fullName)
    }

    fun updateAvatarUri(avatarUri: String?) {
        val currentProfile = _userProfile.value
        _userProfile.value = currentProfile.copy(avatarUri = avatarUri)
    }

    fun updateResumeUrl(resumeUrl: String) {
        val currentProfile = _userProfile.value
        _userProfile.value = currentProfile.copy(resumeUrl = resumeUrl)
    }

    fun updatePosition(position: String) {
        val currentProfile = _userProfile.value
        _userProfile.value = currentProfile.copy(position = position)
    }

    fun saveProfile() {
        viewModelScope.launch {
            profileRepository.saveUserProfile(_userProfile.value)
        }
    }

    fun clearProfile() {
        viewModelScope.launch {
            profileRepository.clearUserProfile()
            _userProfile.value = UserProfile()
        }
    }
}

