plugins {
    kotlin("jvm") version "1.8.22"
    id("com.google.devtools.ksp") version "1.8.22-1.0.11"
}

allprojects {
    apply(plugin = "kotlin")

    repositories {
        mavenCentral()
        maven { setUrl("https://jitpack.io") }
    }

    dependencies {
        implementation("org.slf4j:slf4j-api:2.0.7")
    }

    apply(plugin = "kotlin")

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
}

val kotshiVersion = "2.11.4"

subprojects {
    apply(plugin = "com.google.devtools.ksp")

    dependencies {
        implementation(kotlin("stdlib"))
        implementation("se.ansman.kotshi:api:$kotshiVersion")
        ksp("se.ansman.kotshi:compiler:$kotshiVersion")

        testImplementation(kotlin("test"))
    }
}

dependencies {
    implementation(project(":users"))
    implementation(libs.http4k.serverless.lambda)
    implementation(libs.http4k.format.argo)
    implementation(libs.utils)
    implementation(libs.slf4j.simple)
}
