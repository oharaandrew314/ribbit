dependencies {
    api(libs.forkhandles.values4k)
    implementation(libs.nimbus)
    implementation(libs.nimbus.kms)
    implementation(libs.http4k.connect.amazon.kms)
    api("io.github.oshai:kotlin-logging-jvm:4.0.2")
    api(libs.http4k.cloudnative)
    api(libs.http4k.contract)
    api(libs.http4k.format.moshi) {
        exclude("org.jetbrains.kotlin", "kotlin-reflect")
    }

    testFixturesApi(libs.http4k.core)
    testFixturesApi(libs.forkhandles.result4k)
    testFixturesApi(libs.http4k.connect.amazon.kms)
    testFixturesApi(libs.kotest.assertions.core)
    testFixturesApi(libs.forkhandles.result4k.kotest)
    testFixturesApi(libs.http4k.testing.kotest)
    testFixturesApi(libs.http4k.testing.approval)
    testFixturesApi(libs.slf4j.simple)
}