dependencies {
    api(project(":core"))
    api(libs.forkhandles.result4k)
    implementation(libs.http4k.connect.amazon.dynamodb)
    implementation(libs.http4k.connect.amazon.kms)
    implementation(libs.nimbus)
    implementation(libs.nimbus.kms)

    testImplementation(libs.http4k.connect.amazon.kms.fake)
    testImplementation(libs.http4k.connect.amazon.dynamodb.fake)
    testFixturesApi(testFixtures(project(":core")))
}