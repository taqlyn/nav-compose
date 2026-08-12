plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

group = "com.taqlyn.nav"
version = "0.1.0-SNAPSHOT"

android {
    namespace = "com.taqlyn.nav.compose.navigation3"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
        consumerProguardFiles("consumer-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}

dependencies {
    api(project(":model"))

    testImplementation("junit:junit:4.13.2")
    testImplementation("com.google.truth:truth:1.4.4")
}
