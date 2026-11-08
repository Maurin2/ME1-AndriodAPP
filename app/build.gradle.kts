plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.me1_app"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.me1_app"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
}
