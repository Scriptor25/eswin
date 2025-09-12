plugins {
    id("java")
    id("application")

    id("org.openjfx.javafxplugin") version "0.1.0"
}

group = "io.scriptor"
version = "1.0.0"

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation(libs.jetbrains.annotations)
    implementation(libs.flatlaf)
    implementation(libs.postgresql)
}

java {
    sourceCompatibility = JavaVersion.VERSION_24
    targetCompatibility = JavaVersion.VERSION_24

    toolchain {
        languageVersion = JavaLanguageVersion.of(24)
    }
}

application {
    mainClass = "io.scriptor.eswin.Main"

    applicationName = "eswin"
    applicationDefaultJvmArgs = listOf("--enable-native-access=ALL-UNNAMED")
}

javafx {
    version = "24.0.2"
    modules = listOf("javafx.controls", "javafx.swing", "javafx.web")
}
