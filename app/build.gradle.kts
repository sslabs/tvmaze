plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.navigation.safeargs)
}

android {
    namespace = "org.sslabs.tvmaze"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "org.sslabs.tvmaze"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
            arg("room.incremental", "true")
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            isDebuggable = true
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
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
        viewBinding = true
        buildConfig = true
        compose = true
    }
}

dependencies {
    // Kotlin
    implementation(libs.kotlin.stdlib)

    // AndroidX Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)

    // Constraint Layout
    implementation(libs.androidx.constraintlayout)

    // Fragment extensions
    implementation(libs.androidx.fragment.ktx)

    // Lifecycle components
    implementation(libs.bundles.lifecycle)

    // Material design
    implementation(libs.material)

    // Coordinator layout
    implementation(libs.androidx.coordinatorlayout)

    // Dialogs
    implementation(libs.material.dialogs)

    // Navigator
    implementation(libs.bundles.navigation)
    androidTestImplementation(libs.androidx.navigation.testing)

    // Biometric
    implementation(libs.androidx.biometric)

    // Preference
    implementation(libs.androidx.preference.ktx)

    // Data store
    implementation(libs.datastore.preferences)

    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.fragment)
    ksp(libs.hilt.compiler)
    testImplementation(libs.hilt.android.testing)
    kspTest(libs.hilt.compiler)

    // Retrofit
    implementation(libs.bundles.retrofit)

    // Room
    implementation(libs.bundles.room)
    ksp(libs.room.compiler)

    // Timber
    implementation(libs.timber)

    // Leak Canary
    debugImplementation(libs.leakcanary)

    // Glide (will be replaced by Coil later)
    implementation(libs.glide)
    ksp(libs.glide.compiler)

    // Compose
    implementation(platform(libs.compose.bom))
    implementation(libs.bundles.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.hilt.navigation.compose)
    debugImplementation(libs.bundles.compose.debug)

    // Coil for Compose image loading
    implementation(libs.coil.compose)

    // Core unit test libraries
    testImplementation(libs.junit)
    testImplementation(libs.mockito)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.mockwebserver)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.turbine)

    // Core android test libraries
    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.espresso)
}
