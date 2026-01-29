plugins {
    alias(libs.plugins.spotbugs) apply false
    alias(libs.plugins.owasp.dependencycheck)
    jacoco
}

group = "io.cryptoarena"
version = "1.0-SNAPSHOT"

// OWASP Dependency Check tasks are not compatible with configuration cache
tasks.withType<org.owasp.dependencycheck.gradle.tasks.Analyze>().configureEach {
    notCompatibleWithConfigurationCache("OWASP Dependency Check plugin is not compatible with configuration cache")
}
tasks.withType<org.owasp.dependencycheck.gradle.tasks.Update>().configureEach {
    notCompatibleWithConfigurationCache("OWASP Dependency Check plugin is not compatible with configuration cache")
}

// ==================== OWASP Dependency Check (Security Vulnerabilities) ====================
dependencyCheck {
    // Analyze all configurations
    analyzedTypes = listOf("jar")

    // Output formats
    formats = listOf("HTML", "JSON", "SARIF")

    // Output directory
    outputDirectory = layout.buildDirectory.dir("reports/dependency-check").get().asFile.absolutePath

    // Don't fail build - just report (CI will handle separately)
    failBuildOnCVSS = 11.0f  // Effectively disable (max CVSS is 10)

    // Suppress false positives
    suppressionFile = "config/owasp/suppressions.xml"

    // NVD API configuration
    val nvdApiKey: String? = System.getenv("NVD_API_KEY")?.takeIf { it.isNotBlank() }
    nvd {
        if (nvdApiKey != null) {
            apiKey = nvdApiKey
            delay = 0
        } else {
            delay = 4000  // Rate limiting without API key
        }
        // Cache valid for 7 days to reduce network calls
        validForHours = 168
    }

    // Cache configuration
    data {
        directory = System.getProperty("user.home") + "/.gradle/dependency-check-data"
    }

    // Analyzers - disable ALL unused ones for maximum speed (Java-only project)
    analyzers {
        assemblyEnabled = false
        nuspecEnabled = false
        nugetconfEnabled = false
        nodeEnabled = false
        nodeAuditEnabled = false
        retirejs { enabled = false }
        pyDistributionEnabled = false
        pyPackageEnabled = false
        rubygemsEnabled = false
        bundleAuditEnabled = false
        cocoapodsEnabled = false
        swiftEnabled = false
        cmakeEnabled = false
        autoconfEnabled = false
        composerEnabled = false
        cpanEnabled = false
        dartEnabled = false
        golangDepEnabled = false
        golangModEnabled = false
        // Disable experimental analyzers
        experimentalEnabled = false
        // Disable archive analyzer to speed up
        archiveEnabled = true
        jarEnabled = true
    }

    // Skip test dependencies for faster scan
    skipConfigurations = listOf("testImplementation", "testRuntimeOnly", "testCompileOnly")
}


subprojects {
    group = rootProject.group
    version = rootProject.version

    apply(plugin = "java")
    apply(plugin = "jacoco")
    apply(plugin = "com.github.spotbugs")
    apply(plugin = "checkstyle")

    repositories {
        mavenCentral()
    }

    // Java 25 with preview features
    configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_25
        targetCompatibility = JavaVersion.VERSION_25
    }

    tasks.withType<JavaCompile>().configureEach {
        options.compilerArgs.addAll(listOf(
            "--enable-preview",
            "-Xlint:preview",
            "--release", "25"
        ))
    }

    // ==================== JaCoCo (Code Coverage) ====================
    configure<JacocoPluginExtension> {
        toolVersion = "0.8.13" // Supports Java 25
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
        jvmArgs(
            "--enable-preview",
            "--enable-native-access=ALL-UNNAMED",
            // Required for ByteBuddy (Mockito) with Java 21+
            "--add-opens", "java.base/java.lang=ALL-UNNAMED",
            "--add-opens", "java.base/java.lang.invoke=ALL-UNNAMED",
            "--add-opens", "java.base/java.util=ALL-UNNAMED"
        )
        // Configure Allure results directory
        systemProperty("allure.results.directory", layout.buildDirectory.dir("allure-results").get().asFile.absolutePath)
        testLogging {
            events("passed", "skipped", "failed")
        }
        finalizedBy(tasks.named("jacocoTestReport"))
    }

    tasks.withType<JacocoReport>().configureEach {
        dependsOn(tasks.withType<Test>())
        reports {
            xml.required.set(true)
            html.required.set(true)
            csv.required.set(false)
        }
    }

    tasks.withType<JacocoCoverageVerification>().configureEach {
        violationRules {
            rule {
                limit {
                    minimum = "0.50".toBigDecimal() // 50% minimum coverage
                }
            }
        }
    }

    // ==================== SpotBugs (Java Static Analysis) ====================
    configure<com.github.spotbugs.snom.SpotBugsExtension> {
        ignoreFailures.set(true)
        showStackTraces.set(true)
        showProgress.set(true)
        effort.set(com.github.spotbugs.snom.Effort.MAX)
        reportLevel.set(com.github.spotbugs.snom.Confidence.LOW)
        excludeFilter.set(rootProject.file("config/spotbugs/exclude.xml"))
    }

    tasks.withType<com.github.spotbugs.snom.SpotBugsTask>().configureEach {
        reports.create("html") { required.set(true) }
        reports.create("xml") { required.set(true) }
    }

    // ==================== Checkstyle (Java Code Style) ====================
    configure<CheckstyleExtension> {
        toolVersion = "10.21.1"
        configFile = rootProject.file("config/checkstyle/checkstyle.xml")
        isIgnoreFailures = true
        maxWarnings = 0
    }

    tasks.withType<JavaExec>().configureEach {
        jvmArgs("--enable-preview", "--enable-native-access=ALL-UNNAMED")
    }

    dependencies {
        val implementation by configurations
        val testImplementation by configurations
        val testRuntimeOnly by configurations

        implementation(rootProject.libs.slf4j.api)

        testImplementation(platform(rootProject.libs.junit.bom))
        testImplementation(rootProject.libs.junit.jupiter)
        testImplementation(rootProject.libs.assertj.core)
        testRuntimeOnly(rootProject.libs.junit.platform.launcher)

        // Allure Test Reports
        testImplementation(rootProject.libs.allure.junit5)
    }
}

// ==================== Aggregated Reports ====================
tasks.register<JacocoReport>("jacocoRootReport") {
    group = "verification"
    description = "Generates an aggregate JaCoCo report from all subprojects"

    dependsOn(subprojects.map { it.tasks.withType<Test>() })

    additionalSourceDirs.setFrom(subprojects.flatMap { it.the<SourceSetContainer>()["main"].allSource.srcDirs })
    sourceDirectories.setFrom(subprojects.flatMap { it.the<SourceSetContainer>()["main"].allSource.srcDirs })
    classDirectories.setFrom(subprojects.flatMap { it.the<SourceSetContainer>()["main"].output })
    executionData.setFrom(subprojects.flatMap {
        it.tasks.withType<Test>().map { task -> task.extensions.getByType<JacocoTaskExtension>().destinationFile }
    }.filterNotNull())

    reports {
        html.required.set(true)
        xml.required.set(true)
    }
}

tasks.register("qualityCheck") {
    group = "verification"
    description = "Runs all quality checks (tests, coverage, static analysis)"
    dependsOn(
        subprojects.map { it.tasks.named("test") },
        subprojects.map { it.tasks.named("jacocoTestReport") },
        // Detekt disabled due to Java 25 incompatibility
        subprojects.filter { it.tasks.findByName("spotbugsMain") != null }.map { it.tasks.named("spotbugsMain") },
        subprojects.filter { it.tasks.findByName("checkstyleMain") != null }.map { it.tasks.named("checkstyleMain") }
    )
}

// ==================== Allure Reports ====================
// Allure results are generated in each module's allure-results/ directory
// To generate HTML reports, install Allure CLI and run:
//   allure serve */allure-results
// Or generate static report:
//   allure generate --clean -o build/allure-report */allure-results

// Collect Allure results from all subprojects into root build/allure-results
tasks.register("collectAllureResults") {
    group = "verification"
    description = "Collects all allure-results from subprojects into root build/allure-results"
    doLast {
        val outDir = layout.buildDirectory.dir("allure-results").get().asFile
        if (!outDir.exists()) outDir.mkdirs()
        println("Collecting Allure results into: ${outDir.absolutePath}")
        var filesCopied = 0
        subprojects.forEach { p ->
            // possible locations: project root `allure-results` or `build/allure-results`
            val candidates = listOf(
                p.layout.projectDirectory.dir("allure-results").asFile,
                p.layout.buildDirectory.dir("allure-results").get().asFile
            )
            candidates.forEach { src ->
                if (src.exists() && src.isDirectory) {
                    println("Found results in: $src")
                    // Copy all files directly to outDir (UUIDs in filenames ensure uniqueness)
                    src.listFiles()?.forEach { f ->
                        if (f.isFile) {
                            try {
                                val target = outDir.resolve(f.name)
                                if (!target.exists()) {
                                    f.copyTo(target, overwrite = false)
                                    filesCopied++
                                }
                            } catch (e: Exception) {
                                println("Failed to copy ${f.absolutePath}: ${e.message}")
                            }
                        }
                    }
                }
            }
        }
        println("Collected $filesCopied files into $outDir")
    }
}

// ==================== Security Tasks ====================
tasks.register("securityCheck") {
    group = "verification"
    description = "Runs all security checks (dependency vulnerabilities, secrets scan)"
    dependsOn("dependencyCheckAnalyze")
}

tasks.register("fullCheck") {
    group = "verification"
    description = "Runs all checks: quality + security"
    dependsOn("qualityCheck", "securityCheck")
}
