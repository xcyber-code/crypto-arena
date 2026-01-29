plugins {
    alias(libs.plugins.jmh)
}

description = "JMH Benchmarks for crypto-arena"

// JMH tasks are not compatible with configuration cache
tasks.withType<me.champeau.jmh.JmhBytecodeGeneratorTask>().configureEach {
    notCompatibleWithConfigurationCache("JMH plugin is not compatible with configuration cache")
}
tasks.matching { it.name.startsWith("jmh") }.configureEach {
    notCompatibleWithConfigurationCache("JMH plugin is not compatible with configuration cache")
}

dependencies {
    // Modules to benchmark
    implementation(project(":common"))
    implementation(project(":crypto-arena-engine"))

    // JMH dependencies
    jmh(libs.jmh.core)
    jmhAnnotationProcessor(libs.jmh.generator.annprocess)
}

// JMH Configuration
jmh {
    warmupIterations = 2
    iterations = 5
    fork = 2
    threads = 1
    benchmarkMode = listOf("thrpt", "avgt")
    timeUnit = "ms"
    resultFormat = "JSON"
    resultsFile = project.file("${layout.buildDirectory.get()}/reports/jmh/results.json")
    failOnError = true
    forceGC = true
    jvmArgs = listOf("--enable-preview", "--enable-native-access=ALL-UNNAMED")
}

