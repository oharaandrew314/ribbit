dependencies {
    api(project(":core"))
    api(project(":users"))
    implementation(libs.slf4j.simple)
    api(libs.forkhandles.values4k)
    api(libs.forkhandles.result4k)
    implementation(libs.utils)
    implementation(libs.http4k.cloudnative)

    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.forkhandles.result4k.kotest)
}