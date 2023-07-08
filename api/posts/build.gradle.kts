dependencies {
    api(project(":core"))
    api(project(":users"))
    api(project(":subs"))
    api(libs.forkhandles.result4k)
    implementation(libs.utils)

    testImplementation(testFixtures(project(":core")))
}