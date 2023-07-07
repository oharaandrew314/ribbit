dependencies {
    api(project(":core"))
    api(project(":users"))
    api(project(":subs"))
    api(libs.forkhandles.result4k)
    implementation(libs.utils)
    api(libs.http4k.cloudnative)
    api(libs.http4k.contract)
    implementation(libs.http4k.format.moshi) {
        exclude("org.jetbrains.kotlin", "kotlin-reflect")
    }

    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.forkhandles.result4k.kotest)
}