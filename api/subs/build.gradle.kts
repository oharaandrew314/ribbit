dependencies {
    api(project(":core"))
    api(project(":users"))
    api(libs.forkhandles.result4k)
    implementation(libs.utils)
    implementation(libs.http4k.connect.amazon.dynamodb)

    testImplementation(libs.http4k.connect.amazon.dynamodb.fake)
    testImplementation(libs.http4k.connect.amazon.kms.fake)
    testImplementation(testFixtures(project(":core")))
}