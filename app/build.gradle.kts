import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import java.io.FileInputStream
import java.util.*

plugins {
    id(Android.applicationPlugin)
    id(Kotlin.androidPlugin)
    id(Kotlin.parcelizePlugin)
    id(AndroidGitVersion.plugin).version(AndroidGitVersion.version)
    id(Firebase.googleServicesPlugin)
    id(Firebase.crashlyticsPlugin)
}

androidGitVersion {
    format = "%tag%%-commit%%-dirty%"
    codeFormat = "MMNNPPBBB"
}

val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
try {
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))
} catch (e: java.io.FileNotFoundException) {
    keystoreProperties.setProperty("keyAlias", "")
    keystoreProperties.setProperty("keyPassword", "")
    keystoreProperties.setProperty("storeFile", "/")
    keystoreProperties.setProperty("storePassword", "")
}

android {
    compileSdk = Application.targetSdk
    namespace = Application.packageName

    defaultConfig {
        applicationId = Application.packageName
        minSdk = Application.minSdk
        targetSdk = Application.targetSdk
        versionCode = androidGitVersion.code().takeIf { it > 0 } ?: 1
        versionName = androidGitVersion.name().takeIf { it.isNotEmpty() } ?: "1.0"
    }

    signingConfigs {
        create("release") {
            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
            storeFile = file(keystoreProperties["storeFile"] as String)
            storePassword = keystoreProperties["storePassword"] as String
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles("proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
        }

        getByName("debug") {
            applicationIdSuffix = ".debug"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = Compose.compilerVersion
    }

    applicationVariants.all {
        outputs.all {
            val nameBuilder = StringBuilder()
            nameBuilder.append(applicationId)
            nameBuilder.append("_$name")
            nameBuilder.append("_$versionName")
            nameBuilder.append("_$versionCode")
            nameBuilder.append(".apk")

            (this as BaseVariantOutputImpl).outputFileName =
                if (name.toLowerCase().contains("release")) {
                    "dartify.apk"
                } else {
                    nameBuilder.toString()
                }
        }
    }
}

dependencies {
    implementation(AndroidX.core)

    implementation(Compose.ActivityCompose)
    implementation(Compose.ViewModel)
    implementation(Compose.Material)
    implementation(Compose.Ui)
    implementation(Compose.UiToolingPreview)
    implementation(Compose.NavigationReimagined)
    implementation(Compose.Accompanist.SystemUIController)
    implementation(Compose.Accompanist.Pager)
    debugImplementation(Compose.UiTooling)

    implementation(Koin.compose)

    implementation(platform(Firebase.firebaseBom))
    implementation(Firebase.analytics)
    implementation(Firebase.crashlytics)

    implementation(project(":domain"))
    implementation(project(":errors"))
    implementation(project(":local_datasource"))
    implementation(project(":reorderable_list"))
}
