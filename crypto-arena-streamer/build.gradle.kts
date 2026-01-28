description = "Real-time data streaming for crypto-arena"

dependencies {
    implementation(project(":common"))

    implementation(libs.okhttp)
    implementation(libs.bundles.jackson)
    implementation(libs.bundles.logging)
}
