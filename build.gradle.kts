import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.20"
    id("org.sonarqube") version "3.5.0.2730"
    application
}

group = "net.silve"
version = "1.0-SNAPSHOT"


repositories {
    mavenCentral()
    maven {
        url = uri("https://maven.pkg.github.com/rsilve/smtpc")
        mavenContent {
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
                password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
            }
        }
    }
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
    implementation("net.silve:smtpc:1.0-SNAPSHOT")
    val mockkVersion = "1.13.2"
    testImplementation(kotlin("test"))
    testImplementation("io.mockk:mockk:${mockkVersion}")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "16"
}

application {
    mainClass.set("MainKt")
}

sonarqube {
    properties {
        property("sonar.projectKey", "rsilve_smtpc-tester")
        property("sonar.organization", "rsilve")
        property("sonar.host.url", "https://sonarcloud.io")
    }
}