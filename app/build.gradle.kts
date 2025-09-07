plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.green"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.green"
        minSdk = 21
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    
    // Enable aapt2 for better resource processing
    aaptOptions {
        noCompress += listOf("pt")
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.activity:activity:1.8.2")
    
    // PyTorch Mobile
    implementation("org.pytorch:pytorch_android:1.13.1")
    implementation("org.pytorch:pytorch_android_torchvision:1.13.1")
    implementation(libs.fragment)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}