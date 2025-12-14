    plugins {
        alias(libs.plugins.android.application)
        id("com.google.gms.google-services")   // Firebase plugin
    }

    android {
        namespace = "com.d3f4ult.kanbanapp"
        compileSdk = 36

        defaultConfig {
            applicationId = "com.d3f4ult.kanbanapp"
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
        // Firebase
        implementation(platform("com.google.firebase:firebase-bom:33.5.1"))
        implementation("com.google.firebase:firebase-firestore")
        implementation("com.google.firebase:firebase-auth")

        // Shimmer effect
        implementation("com.facebook.shimmer:shimmer:0.5.0")

        // AndroidX / UI
        implementation(libs.appcompat)
        implementation(libs.material)
        implementation(libs.activity)
        implementation(libs.constraintlayout)

        // Tests
        testImplementation(libs.junit)
        androidTestImplementation(libs.ext.junit)
        androidTestImplementation(libs.espresso.core)
    }
