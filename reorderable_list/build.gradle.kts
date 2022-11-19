plugins {
    id(Kotlin.gradlePlugin)
    id(Android.libraryPlugin)
}

android {
    compileSdk = Application.targetSdk
    namespace = "${Application.packageName}.reoderable.list"

    defaultConfig {
        minSdk = Application.minSdk
        targetSdk = Application.targetSdk
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = Compose.compilerVersion
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
}

dependencies {
    implementation(Compose.Ui)
    implementation(Compose.Material)
}
