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
val slf4jVersion = "2.0.7"

dependencies {
    implementation(platform("org.http4k:http4k-bom:5.4.0.0"))
    implementation(platform("org.http4k:http4k-connect-bom:5.1.3.0"))
    implementation(platform("dev.forkhandles:forkhandles-bom:2.6.0.0"))

    implementation(kotlin("stdlib"))
    implementation("org.http4k:http4k-core")
    implementation("org.http4k:http4k-serverless-lambda")
    implementation("org.http4k:http4k-cloudnative")
    implementation("org.http4k:http4k-contract")
    implementation("org.http4k:http4k-format-moshi") {
        exclude("org.jetbrains.kotlin", "kotlin-reflect")
    }
    implementation("org.http4k:http4k-connect-amazon-dynamodb")
    implementation("dev.forkhandles:result4k")
    implementation("dev.forkhandles:values4k")
    implementation("com.github.oharaandrew314:service-utils:1.5.0")
    implementation("com.nimbusds:nimbus-jose-jwt:9.26")
    implementation("io.github.oshai:kotlin-logging-jvm:4.0.2")
    implementation("org.slf4j:slf4j-api:$slf4jVersion")
    runtimeOnly("org.slf4j:slf4j-simple:$slf4jVersion")
    implementation("se.ansman.kotshi:api:$kotshiVersion")
    ksp("se.ansman.kotshi:compiler:$kotshiVersion")
    implementation("com.github.ksuid:ksuid:1.1.2")

    testImplementation(kotlin("test"))
    testImplementation("dev.forkhandles:result4k-kotest")
    testImplementation("org.http4k:http4k-testing-kotest")
    testImplementation("org.http4k:http4k-testing-approval")
    testImplementation("org.http4k:http4k-connect-amazon-dynamodb-fake")
}
