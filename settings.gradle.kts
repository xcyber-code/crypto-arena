rootProject.name = "crypto-arena"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}


dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)
    repositories {
        mavenCentral()
    }
}

include(
    ":common",
    ":crypto-arena-engine",
    ":crypto-arena-streamer",
    ":crypto-arena-registry",
    ":crypto-arena-benchmarks",
    ":crypto-arena-stress"
)
