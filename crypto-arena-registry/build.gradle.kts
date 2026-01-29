description = "Service registry and discovery for crypto-arena"

dependencies {
    implementation(project(":common"))

    implementation(libs.bundles.jackson)
    implementation(libs.bundles.logging)
}
