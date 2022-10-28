plugins {
    id(Kotlin.gradlePlugin)
    id(Android.libraryPlugin)
}

android {
    compileSdk = Application.targetSdk
    namespace = "${Application.packageName}.local.datasource"

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
    implementation(Kotlin.Coroutines.core)

    implementation(project(":errors"))
    implementation(project(":datasource"))
    implementation(project(":rumble"))

    testImplementation(kotlin("test"))
    testImplementation(Kotest.core)
    testImplementation(Kotest.assertions)
    testImplementation(Mockk.core)
}
