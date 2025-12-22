package com.example.practice3.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
// Using simple icon
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.practice3.di.AppContainer
import com.example.practice3.model.UserProfile
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onNavigateBack: () -> Unit = {}
) {
    val viewModel = remember { AppContainer.createProfileViewModel() }
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var showImageSourceDialog by remember { mutableStateOf(false) }

    // Создаем временный файл для фото с камеры
    val tempPhotoFile = remember {
        File(context.cacheDir, "temp_photo.jpg")
    }

    val tempPhotoUri = remember {
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            tempPhotoFile
        )
    }

    // Launcher для выбора из галереи
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val permanentUri = copyUriToInternalStorage(context, it)
            permanentUri?.let { permanent ->
                viewModel.updateAvatarUri(permanent.toString())
            }
        }
    }

    // Launcher для камеры
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            viewModel.updateAvatarUri(tempPhotoUri.toString())
        }
    }

    // Launcher для запроса разрешений камеры
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch(tempPhotoUri)
        } else {
            Toast.makeText(
                context,
                "Необходимо разрешение для доступа к камере",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Launcher для запроса разрешений галереи
    val galleryPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            galleryLauncher.launch("image/*")
        } else {
            Toast.makeText(
                context,
                "Необходимо разрешение для доступа к галерее",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Редактирование профиля") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            viewModel.saveProfile()
                            onNavigateBack()
                        }
                    ) {
                        Text("Готово")
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Аватар
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable {
                        showImageSourceDialog = true
                    },
                contentAlignment = Alignment.Center
            ) {
                if (userProfile.avatarUri != null) {
                    AsyncImage(
                        model = userProfile.avatarUriObj,
                        contentDescription = "Аватар пользователя",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Оверлей с иконкой камеры
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "📷",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Поля ввода
            OutlinedTextField(
                value = userProfile.fullName,
                onValueChange = { viewModel.updateFullName(it) },
                label = { Text("ФИО") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = userProfile.position,
                onValueChange = { viewModel.updatePosition(it) },
                label = { Text("Должность") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = userProfile.resumeUrl,
                onValueChange = { viewModel.updateResumeUrl(it) },
                label = { Text("Ссылка на резюме") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }
    }

    // Диалог выбора источника изображения
    if (showImageSourceDialog) {
        AlertDialog(
            onDismissRequest = { showImageSourceDialog = false },
            title = { Text("Выбрать фото") },
            text = { Text("Откуда взять фото?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showImageSourceDialog = false
                        checkGalleryPermissionsAndLaunch(context, galleryPermissionLauncher, galleryLauncher)
                    }
                ) {
                    Text("Галерея")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showImageSourceDialog = false
                        checkCameraPermissionsAndLaunch(context, cameraPermissionLauncher, tempPhotoUri, cameraLauncher)
                    }
                ) {
                    Text("Камера")
                }
            }
        )
    }
}

private fun checkCameraPermissionsAndLaunch(
    context: Context,
    permissionLauncher: androidx.activity.result.ActivityResultLauncher<String>,
    tempPhotoUri: Uri,
    cameraLauncher: androidx.activity.result.ActivityResultLauncher<Uri>
) {
    val permission = Manifest.permission.CAMERA
    val isGranted = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

    if (isGranted) {
        cameraLauncher.launch(tempPhotoUri)
    } else {
        permissionLauncher.launch(permission)
    }
}

private fun checkGalleryPermissionsAndLaunch(
    context: Context,
    permissionLauncher: androidx.activity.result.ActivityResultLauncher<Array<String>>,
    galleryLauncher: androidx.activity.result.ActivityResultLauncher<String>
) {
    val permissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.READ_MEDIA_IMAGES
    ).filter { permission ->
        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    if (permissions.size >= 1) { // Хотя бы одно разрешение на чтение есть
        galleryLauncher.launch("image/*")
    } else {
        permissionLauncher.launch(arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_MEDIA_IMAGES
        ))
    }
}

private fun copyUriToInternalStorage(context: Context, sourceUri: Uri): Uri? {
    return try {
        val inputStream = context.contentResolver.openInputStream(sourceUri)
        val fileName = "avatar_${System.currentTimeMillis()}.jpg"
        val outputFile = File(context.filesDir, fileName)

        inputStream?.use { input ->
            outputFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            outputFile
        )
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
