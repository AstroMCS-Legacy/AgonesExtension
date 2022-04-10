import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

// Provide maven credentials in ~/.gradle/gradle.properties or using environment variables
val mavenUsername = System.getenv("MAVEN_REPO_USERNAME") ?: properties["mavenUsername"] as String?
val mavenPassword = System.getenv("MAVEN_REPO_PASSWORD") ?: properties["mavenPassword"] as String?

val mavenCredentials: PasswordCredentials.() -> Unit = {
    username = mavenUsername
    password = mavenPassword
}

// Getting the project's groupId, artifactId and version.
val projectGroupId = System.getenv("BUILD_GROUP_ID") ?: properties["groupId"] as String? ?: ""
val projectArtifactId = System.getenv("BUILD_ARTIFACT_ID") ?: properties["artifactId"] as String? ?: ""
val projectVersion = System.getenv("BUILD_VERSION") ?: properties["version"] as String? ?: ""

group = projectGroupId
version = projectVersion

plugins {
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.serialization") version "1.6.10"

    id("com.github.johnrengelman.shadow") version "7.1.0"

    `maven-publish`
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

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = projectGroupId
            artifactId = projectArtifactId
            version = projectVersion

            from(components["kotlin"])
        }
    }

    repositories {
        maven {
            val ending = if (projectVersion.endsWith("-SNAPSHOT")) { "snapshots" } else { "releases" }
            url = uri("https://repo.astromc.gg/repository/maven-$ending/")
            credentials(mavenCredentials)
        }
    }
}
