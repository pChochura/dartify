object Kotlin {
    const val version = "1.7.20"
    const val gradlePlugin = "org.jetbrains.kotlin.android"
    const val parcelizePlugin = "kotlin-parcelize"
    const val androidPlugin = "kotlin-android"
    const val javaPlugin = "kotlin"

    object Coroutines {
        private const val version = "1.6.4"
        const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$version"
        const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
    }
}

object Firebase {
    const val googleServicesVersion = "4.3.13"
    const val googleServicesPlugin = "com.google.gms.google-services"

    const val crashlyticsVersion = "2.9.2"
    const val crashlyticsPlugin = "com.google.firebase.crashlytics"

    const val firebaseBom = "com.google.firebase:firebase-bom:31.1.0"
    const val analytics = "com.google.firebase:firebase-analytics-ktx"
    const val crashlytics = "com.google.firebase:firebase-crashlytics-ktx"
}

object KSP {
    const val version = "1.7.20-1.0.8"
    const val gradlePlugin = "com.google.devtools.ksp"
}

object Java {
    const val libraryPlugin = "java-library"
}

object Android {
    const val gradlePluginVersion = "7.3.0"
    const val applicationPlugin = "com.android.application"
    const val libraryPlugin = "com.android.library"
}

object AndroidX {
    const val core = "androidx.core:core-ktx:1.8.0"
    const val annotations = "androidx.annotation:annotation:1.4.0"
}

object Compose {
    const val compilerVersion = "1.3.2"
    private const val version = "1.2.1"

    const val ActivityCompose = "androidx.activity:activity-compose:1.5.1"

    const val Ui = "androidx.compose.ui:ui:$version"
    const val UiTooling = "androidx.compose.ui:ui-tooling:$version"
    const val Material = "androidx.compose.material:material:$version"
    const val UiToolingPreview = "androidx.compose.ui:ui-tooling-preview:$version"

    const val ViewModel = "androidx.lifecycle:lifecycle-viewmodel-compose:2.4.1"

    const val NavigationReimagined = "dev.olshevski.navigation:reimagined:1.2.0"

    object Accompanist {
        private const val version = "0.27.1"
        const val SystemUIController =
            "com.google.accompanist:accompanist-systemuicontroller:$version"
        const val Pager ="com.google.accompanist:accompanist-pager:$version"
    }
}

object Detekt {
    const val version = "1.21.0"
    const val gradlePlugin = "io.gitlab.arturbosch.detekt"
    const val ktLintPlugin = "io.gitlab.arturbosch.detekt:detekt-formatting:$version"
}

object AndroidGitVersion {
    const val version = "0.4.14"
    const val plugin = "com.gladed.androidgitversion"
}

object Koin {
    const val compose = "io.insert-koin:koin-androidx-compose:3.3.0"
    const val android = "io.insert-koin:koin-android:3.3.0"
    const val core = "io.insert-koin:koin-core:3.2.2"
}

object Kotest {
    private const val version = "5.5.1"
    const val core = "io.kotest:kotest-runner-junit5-jvm:$version"
    const val assertions = "io.kotest:kotest-assertions-core-jvm:$version"
}

object Mockk {
    private const val version = "1.13.2"
    const val core = "io.mockk:mockk:$version"
}

object RoomDB {
    private const val version = "2.4.3"
    const val ktx = "androidx.room:room-ktx:$version"
    const val runtime = "androidx.room:room-runtime:$version"
    const val compiler = "androidx.room:room-compiler:$version"
}
