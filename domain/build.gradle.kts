plugins {
    id(Java.libraryPlugin)
    id(Kotlin.javaPlugin)
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    implementation(Koin.core)
    implementation(Kotlin.Coroutines.core)

    implementation(project(":errors"))
    implementation(project(":datasource"))

    testImplementation(kotlin("test"))
    testImplementation(Kotest.core)
    testImplementation(Kotest.assertions)
    testImplementation(Mockk.core)
}
