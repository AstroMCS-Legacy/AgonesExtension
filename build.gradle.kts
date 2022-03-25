import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.serialization") version "1.6.10"

    id("com.github.johnrengelman.shadow") version "7.1.0"
}

group = "gg.astromc"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
    implementation("com.charleskorn.kaml:kaml:0.40.0")

    compileOnly("com.github.Minestom:Minestom:b63a73516d")

    implementation("com.github.Cubxity:AgonesKt:4a8f3dc251")
    runtimeOnly("io.grpc:grpc-kotlin-stub:0.2.0")
    runtimeOnly("io.grpc:grpc-netty:1.45.0")
}

tasks {
    shadowJar {
        archiveBaseName.set(project.name)
        minimize()
    }

    build {
        dependsOn(shadowJar)
    }
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = JavaVersion.VERSION_17.toString()
    freeCompilerArgs = listOf("-Xinline-classes", "-Xopt-in=kotlin.RequiresOptIn")
}
