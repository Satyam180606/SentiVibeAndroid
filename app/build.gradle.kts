import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.application")
    id("kotlin-android") // Using the classic ID
    id("com.google.gms.google-services")
}

// Load properties from local.properties file
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(FileInputStream(localPropertiesFile))
}

android {
    namespace = "com.example.sentivibe"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.sentivibe"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // X (Twitter) API Keys
        buildConfigField(
            "String",
            "X_API_KEY",
            "\"${localProperties.getProperty("twitter.apiKey", "YOUR_KEY_HERE")}\""
        )
        buildConfigField(
            "String",
            "X_API_SECRET",
            "\"${localProperties.getProperty("twitter.apiSecret", "YOUR_KEY_HERE")}\""
        )
        // X (Twitter) OAuth 2.0 Client Keys
        buildConfigField(
            "String",
            "X_CLIENT_ID",
            "\"${localProperties.getProperty("twitter.clientId", "YOUR_KEY_HERE")}\""
        )
        buildConfigField(
            "String",
            "X_CLIENT_SECRET",
            "\"${localProperties.getProperty("twitter.clientSecret", "YOUR_KEY_HERE")}\""
        )

        buildConfigField(
            "String",
            "REDDIT_API_KEY",
            "\"${localProperties.getProperty("reddit.apiKey", "YOUR_KEY_HERE")}\""
        )
        buildConfigField(
            "String",
            "META_API_KEY",
            "\"${localProperties.getProperty("meta.apiKey", "YOUR_KEY_HERE")}\""
        )
    }

    buildTypes {
        debug {
            buildConfigField("String", "API_BASE_URL", "\"https://sentivibe-android-backend.onrender.com/\"")
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField(
                "String",
                "API_BASE_URL",
                "\"https://sentivibe-android-backend.onrender.com/\""
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    packaging {
        resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
}

dependencies {
    // Core & UI
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Lifecycle, ViewModel, and LiveData (for MVVM Architecture)
    val lifecycle_version = "2.7.0"
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.1.0"))
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-auth")


    // Networking
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    // Charting
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
