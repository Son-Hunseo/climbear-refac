import org.jetbrains.kotlin.konan.properties.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    kotlin("kapt")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "com.example.climbear"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.climbear"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val properties = Properties()
        properties.load(project.rootProject.file("local.properties").inputStream())

        val nativeAppKey = properties.getProperty("NATIVE_APP_KEY")

        buildConfigField("String", "KAKAO_NATIVE_KEY", properties.getProperty("KAKAO_NATIVE_KEY"))
        manifestPlaceholders["NATIVE_APP_KEY"] = nativeAppKey
        manifestPlaceholders["MAPS_API_KEY"] = properties.getProperty("MAPS_API_KEY")
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
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.animation)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)

    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.video)
    implementation(libs.androidx.camera.extensions)

    implementation(libs.coil.kt.coil.compose)

    // 1) Hilt–WorkManager 통합 라이브러리
    implementation("androidx.hilt:hilt-work:1.2.0")
    kapt("androidx.hilt:hilt-compiler:1.0.0")

    // Hilt
    implementation(libs.dagger.hilt.android)
    implementation(libs.androidx.work.runtime.ktx)
    kapt(libs.dagger.hilt.android.compiler)

    // Hilt + Navigation-Compose
    implementation(libs.androidx.hilt.navigation.compose)

    // Media3 ExoPlayer
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.exoplayer.dash)
    implementation(libs.androidx.media3.ui)
    implementation(libs.androidx.media3.ui.compose)

    // Retrofit
    implementation(libs.retrofit2.retrofit)

    implementation(libs.retrofit2.converter.gson)

    // Kakao
    implementation(libs.kakao)

    //MediaPipe
    implementation ("com.google.mediapipe:tasks-vision:latest.release")

    //Gson
    implementation("com.google.code.gson:gson:2.13.1")

    // Google Map
    implementation("com.google.maps.android:maps-compose:4.4.1")

    // Mapbox
    implementation("com.mapbox.extension:maps-compose:11.12.1")
    implementation("com.mapbox.maps:android:11.11.0")
    implementation("com.mapbox.mapboxsdk:mapbox-sdk-geojson:6.12.0")

    // Maps SDK for Android
    implementation("com.google.android.gms:play-services-maps:19.0.0")

    // Security
    implementation (libs.androidx.security)

    // DataStore
    implementation (libs.androidx.datastore.preferences)

    implementation(libs.play.services.location)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}