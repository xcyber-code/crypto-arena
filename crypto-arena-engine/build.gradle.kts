description = "Core trading engine for crypto-arena"

dependencies {
    implementation(project(":common"))

    implementation(libs.bundles.jackson)
    implementation(libs.bundles.logging)
    implementation(libs.bouncycastle)
}
