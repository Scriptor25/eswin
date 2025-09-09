plugins {
    id("application")
    id("java")
    id("idea")
    id("org.openjfx.javafxplugin") version "0.1.0"
}

group = "io.scriptor"
version = "1.0.0"
description = "eswin"

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation(libs.jetbrains.annotations)
    implementation(libs.formdev.flatlaf)
    implementation(libs.postgresql.postgresql)
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
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}

javafx {
    version = "24.0.2"
    modules = listOf("javafx.controls", "javafx.swing", "javafx.web")
}

tasks.withType<JavaExec>().configureEach {
    jvmArgs("--enable-native-access=ALL-UNNAMED")
}
