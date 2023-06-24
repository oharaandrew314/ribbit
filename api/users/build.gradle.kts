dependencies {
    api(project(":core"))
    implementation(libs.slf4j.simple)
    api(libs.forkhandles.values4k)
    api(libs.forkhandles.result4k)
    implementation(libs.utils)
    api(libs.http4k.cloudnative)
    api(libs.http4k.contract)
    api(libs.http4k.format.moshi) {
        exclude("org.jetbrains.kotlin", "kotlin-reflect")
    }

    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.forkhandles.result4k.kotest)
}