plugins {
    kotlin("jvm") version "1.9.0"
    id("com.google.devtools.ksp") version "1.9.0-1.0.11"
    id("java-test-fixtures")
}

allprojects {
    apply(plugin = "kotlin")

    repositories {
        mavenCentral()
        maven { setUrl("https://jitpack.io") }
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
    apply(plugin = "java-test-fixtures")

    dependencies {
        implementation(kotlin("stdlib"))
        implementation("se.ansman.kotshi:api:$kotshiVersion")
        ksp("se.ansman.kotshi:compiler:$kotshiVersion")

        testImplementation(kotlin("test"))
    }
}

dependencies {
    implementation(project(":users"))
    implementation(project(":subs"))
    implementation(project(":posts"))
    implementation(libs.http4k.serverless.lambda)
    implementation(libs.utils)
    implementation(libs.slf4j.simple)
}
