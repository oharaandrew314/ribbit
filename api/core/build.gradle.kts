dependencies {
    api(libs.forkhandles.values4k)
    implementation(libs.nimbus)
    implementation(libs.nimbus.kms)
    implementation(libs.http4k.connect.amazon.kms)

    testFixturesApi(libs.http4k.core)
    testFixturesApi(libs.forkhandles.result4k)
    testFixturesApi(libs.http4k.connect.amazon.kms)
}