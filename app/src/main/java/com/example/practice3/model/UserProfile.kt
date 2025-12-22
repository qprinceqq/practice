package com.example.practice3.model

import android.net.Uri

data class UserProfile(
    val fullName: String = "",
    val avatarUri: String? = null, // URI фотографии на устройстве
    val resumeUrl: String = "", // URL на резюме/портфолио в сети
    val position: String = "" // Должность (дополнительное поле)
) {
    // Проверяем, заполнен ли профиль
    val isEmpty: Boolean
        get() = fullName.isBlank() && avatarUri.isNullOrBlank() &&
                resumeUrl.isBlank() && position.isBlank()

    // Получаем URI аватарки как Uri объект
    val avatarUriObj: Uri?
        get() = avatarUri?.let { Uri.parse(it) }
}

