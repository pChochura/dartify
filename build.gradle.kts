plugins {
    id(Android.applicationPlugin).version(Android.gradlePluginVersion).apply(false)
    id(Kotlin.gradlePlugin).version(Kotlin.version).apply(false)
    id(KSP.gradlePlugin).version(KSP.version).apply(false)
    id(Detekt.gradlePlugin).version(Detekt.version)
    id(Firebase.googleServicesPlugin).version(Firebase.googleServicesVersion).apply(false)
    id(Firebase.crashlyticsPlugin).version(Firebase.crashlyticsVersion).apply(false)
}

subprojects {
    apply(plugin = Detekt.gradlePlugin)

    detekt {
        debug = true
        buildUponDefaultConfig = true
        ignoreFailures = true
        config = files("$rootDir/config/detekt.yml")
        dependencies {
            detektPlugins(Detekt.ktLintPlugin)
        }
    }

    tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
        reports {
            xml.required.set(true)
            html.required.set(false)
            txt.required.set(false)
            sarif.required.set(false)
        }
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
    }
}

task("clean", Delete::class) {
    delete(rootProject.buildDir)
}
