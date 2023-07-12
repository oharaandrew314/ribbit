plugins {
    kotlin("jvm") version "1.9.0"
    id("com.google.devtools.ksp") version "1.9.0-1.0.11"
}


repositories {
    mavenCentral()
    maven { setUrl("https://jitpack.io") }
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

tasks.compileKotlin {
    kotlinOptions {
        allWarningsAsErrors = true
    }
}

val kotshiVersion = "2.11.4"

dependencies {
    implementation(kotlin("stdlib"))
    implementation(libs.http4k.serverless.lambda)
    implementation(libs.utils)
    implementation(libs.http4k.core)
    implementation(libs.http4k.cloudnative)
    implementation(libs.http4k.contract)
    implementation(libs.http4k.format.moshi) {
        exclude("org.jetbrains.kotlin", "kotlin-reflect")
    }
    implementation(libs.http4k.connect.amazon.dynamodb)
    implementation(libs.forkhandles.result4k)
    implementation(libs.forkhandles.values4k)
    implementation(libs.nimbus)
    implementation("io.github.oshai:kotlin-logging-jvm:4.0.2")
    implementation(libs.slf4j.simple)
    implementation("se.ansman.kotshi:api:$kotshiVersion")
    ksp("se.ansman.kotshi:compiler:$kotshiVersion")

    testImplementation(kotlin("test"))
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.forkhandles.result4k.kotest)
    testImplementation(libs.http4k.testing.kotest)
    testImplementation(libs.http4k.testing.approval)
    testImplementation(libs.http4k.connect.amazon.dynamodb.fake)
}
