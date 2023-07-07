dependencies {
    api(project(":core"))
    api(project(":users"))
    api(libs.forkhandles.result4k)
    implementation(libs.utils)
    implementation(libs.http4k.cloudnative)
    implementation(libs.http4k.format.moshi) {
        exclude("org.jetbrains.kotlin", "kotlin-reflect")
    }

    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.forkhandles.result4k.kotest)
}