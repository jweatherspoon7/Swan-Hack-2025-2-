plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.robin.swanhack25"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.robin.swanhack25"
        minSdk = 24
        targetSdk = 36
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.volley)
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation("com.plaid.link:sdk-core:5.5.0")
    implementation("com.squareup.picasso:picasso:2.8")
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
