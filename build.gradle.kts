val ktor_version: String by project
val logback_version: String by project

plugins {
    kotlin("jvm") version "1.8.0"
    application
    id("org.jetbrains.kotlin.plugin.serialization") version "1.8.22"
}

group = "moe.fuqiuluo"
version = "1.1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-content-negotiation-jvm:2.3.2")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:2.3.2")
    implementation("io.ktor:ktor-server-core-jvm:2.3.2")
    implementation("io.ktor:ktor-server-netty-jvm:2.3.2")
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:1.3.3")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")

    implementation(fileTree(mapOf(
        "dir" to "libs",
        "include" to listOf("*.jar")
    )))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}

application {
    mainClass.set("MainKt")
}