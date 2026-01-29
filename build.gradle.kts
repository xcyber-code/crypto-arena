plugins {
    alias(libs.plugins.spotbugs) apply false
    alias(libs.plugins.owasp.dependencycheck)
    jacoco
}

group = "io.cryptoarena"
version = "1.0-SNAPSHOT"

// ==================== OWASP Dependency Check (Security Vulnerabilities) ====================
dependencyCheck {
    // Analyze all configurations
    analyzedTypes = listOf("jar")

    // Output formats
    formats = listOf("HTML", "JSON", "SARIF")

    // Output directory
    outputDirectory = layout.buildDirectory.dir("reports/dependency-check").get().asFile.absolutePath

    // Fail build on CVSS score >= 7 (High/Critical)
    failBuildOnCVSS = 7.0f

    // Suppress false positives
    suppressionFile = "config/owasp/suppressions.xml"

    // NVD API configuration - use API key for 10x faster downloads
    // Get your free key at: https://nvd.nist.gov/developers/request-an-api-key
    // Set via: export NVD_API_KEY=your-key or GitHub Secrets
    val nvdApiKey: String? = System.getenv("NVD_API_KEY")?.takeIf { it.isNotBlank() }
    nvd {
        if (nvdApiKey != null) {
            apiKey = nvdApiKey
            delay = 0  // No delay needed with API key
        } else {
            delay = 3500  // Rate limiting without API key
        }
    }

    // Cache configuration - store in user home to persist across builds
    data {
        directory = System.getProperty("user.home") + "/.gradle/dependency-check-data"
    }

    // Analyzers - disable unused ones for speed (Java-only project)
    analyzers {
        // .NET
        assemblyEnabled = false
        nuspecEnabled = false
        nugetconfEnabled = false

        // JavaScript/Node
        nodeEnabled = false
        nodeAuditEnabled = false
        retirejs {
            enabled = false
        }

        // Python
        pyDistributionEnabled = false
        pyPackageEnabled = false

        // Ruby
        rubygemsEnabled = false
        bundleAuditEnabled = false

        // iOS/Swift
        cocoapodsEnabled = false
        swiftEnabled = false

        // C/C++
        cmakeEnabled = false
        autoconfEnabled = false

        // Other
        composerEnabled = false
        cpanEnabled = false
        dartEnabled = false
        golangDepEnabled = false
        golangModEnabled = false
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
