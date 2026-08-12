plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

group = "com.taqlyn.nav"
version = "0.1.0-SNAPSHOT"

android {
    namespace = "com.taqlyn.nav.compose.navigation2"
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
    implementation("androidx.navigation:navigation-compose:2.8.5")

    testImplementation("junit:junit:4.13.2")
    testImplementation("com.google.truth:truth:1.4.4")
    testImplementation("org.mockito:mockito-core:5.14.2")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.4.0")
}
