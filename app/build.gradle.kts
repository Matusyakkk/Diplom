plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.example.myapplication"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.myapplication"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

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
    packaging {
        resources {
            excludes += setOf(
                "META-INF/DEPENDENCIES",
                "META-INF/INDEX.LIST",
                "META-INF/FastDoubleParser-LICENSE",
                "META-INF/DISCLAIMER",
                "META-INF/versions/9/OSGI-INF/MANIFEST.MF",
                "META-INF/FastDoubleParser-NOTICE",
                "META-INF/io.netty.versions.properties"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation ("io.metamask.androidsdk:metamask-android-sdk:0.5.0")
    implementation("com.google.dagger:hilt-android:2.56.2")
    kapt("com.google.dagger:hilt-android-compiler:2.56.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.9")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")
    implementation("androidx.navigation:navigation-compose:2.8.9")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")
    implementation("com.google.code.gson:gson:2.9.0")
    implementation("androidx.activity:activity-ktx:1.9.0")
    implementation("io.coil-kt.coil3:coil-compose:3.2.0")
    implementation("io.reactivex.rxjava3:rxkotlin:3.0.1")
    implementation("androidx.compose.material3:material3:1.3.2") // Актуальна версія на момент 2025
    implementation("androidx.compose.material:material:1.8.2")
    implementation("net.onyxmueller.pinata:pinata:1.0.1")
    //implementation("androidx.compose.material:material-pullrefresh:1.1.0")
    implementation(libs.web3core)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    //implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}