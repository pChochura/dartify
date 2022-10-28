plugins {
    id(Kotlin.gradlePlugin)
    id(Android.libraryPlugin)
}

android {
    compileSdk = Application.targetSdk
    namespace = "${Application.packageName}.rumble"

    defaultConfig {
        minSdk = Application.minSdk
        targetSdk = Application.targetSdk
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
}

dependencies {
    implementation(Koin.core)

    implementation(project(":errors"))
}
