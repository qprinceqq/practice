plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.ksp)
}

android {
    namespace = "com.example.practice3"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.practice3"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    // --- Compose BOM (реальная стабильная версия, через version catalog)
    implementation(platform(libs.androidx.compose.bom))

    // --- Основные библиотеки Compose
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)

    // --- Activity + ViewModel Compose
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // --- Навигация
    implementation(libs.androidx.navigation.compose)

    // --- ConstraintLayout для Compose
    implementation(libs.androidx.constraintlayout.compose)

    // --- Загрузка изображений (Coil)
    implementation(libs.coil.compose)

    // --- Network dependencies
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.moshi)
    implementation(libs.moshi.kotlin)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)

    // --- Lifecycle LiveData
    implementation(libs.androidx.lifecycle.livedata.ktx)

    // --- Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // --- DataStore
    implementation(libs.androidx.datastore.preferences)

    // --- Activity Result API для камеры и галереи
    implementation(libs.androidx.activity.compose)

    // --- Тесты
    testImplementation(libs.junit)
    testImplementation("androidx.test:core:1.5.0")
    testImplementation("androidx.test.ext:junit-ktx:1.1.5")
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Room testing
    testImplementation("androidx.room:room-testing:2.7.0")

    // Download manager for resume download
    implementation("androidx.work:work-runtime-ktx:2.9.0")
}