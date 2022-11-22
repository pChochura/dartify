plugins {
    id(Kotlin.gradlePlugin)
    id(Android.libraryPlugin)
    id(KSP.gradlePlugin)
}

android {
    compileSdk = Application.targetSdk
    namespace = "${Application.packageName}.local.datasource"

    defaultConfig {
        minSdk = Application.minSdk
        targetSdk = Application.targetSdk
    }

    ksp {
        arg("room.schemaLocation", "$projectDir/schemas")
        arg("room.incremental", "true")
        arg("room.expandProjection", "true")
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

    implementation(RoomDB.ktx)
    implementation(RoomDB.runtime)
    ksp(RoomDB.compiler)

    implementation(project(":errors"))
    implementation(project(":datasource"))
    implementation(project(":rumble"))

    testImplementation(kotlin("test"))
    testImplementation(Kotest.core)
    testImplementation(Kotest.assertions)
    testImplementation(Mockk.core)
}
