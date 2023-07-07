dependencies {
    api(project(":core"))
    api(libs.forkhandles.result4k)
    implementation(libs.utils)
    api(libs.http4k.cloudnative)
    api(libs.http4k.contract)
    implementation(libs.http4k.format.moshi) {
        exclude("org.jetbrains.kotlin", "kotlin-reflect")
    }
    implementation(libs.http4k.connect.amazon.dynamodb)
    implementation(libs.http4k.connect.amazon.kms)
    implementation(libs.nimbus)
    implementation(libs.nimbus.kms)

    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.forkhandles.result4k.kotest)
    testImplementation(libs.http4k.testing.kotest)
    testImplementation(libs.http4k.testing.approval)
    testImplementation(libs.http4k.connect.amazon.kms.fake)
    testImplementation(libs.http4k.connect.amazon.dynamodb.fake)
    testFixturesApi(testFixtures(project(":core")))
}