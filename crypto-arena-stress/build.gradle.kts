description = "JCStress Concurrency Tests for crypto-arena"

// JCStress source set
sourceSets {
    create("jcstress") {
        java.srcDir("src/jcstress/java")
        compileClasspath += sourceSets.main.get().output
        runtimeClasspath += sourceSets.main.get().output
    }
}

val jcstressImplementation: Configuration by configurations.getting {
    extendsFrom(configurations.implementation.get())
}

val jcstressAnnotationProcessor: Configuration by configurations.getting

val jcstressRuntimeClasspath: Configuration by configurations.getting {
    extendsFrom(configurations.runtimeClasspath.get())
}

dependencies {
    // Modules to stress test
    implementation(project(":common"))
    implementation(project(":crypto-arena-engine"))

    // JCStress
    jcstressImplementation(libs.jcstress.core)
    jcstressAnnotationProcessor(libs.jcstress.core)
}

// JCStress JAR task
val jcstressJar by tasks.registering(Jar::class) {
    group = "jcstress"
    description = "Builds the JCStress test JAR"
    archiveClassifier.set("jcstress")

    dependsOn(tasks.named("compileJcstressJava"))
    dependsOn(tasks.named("classes"))

    from(sourceSets["jcstress"].output)
    from(sourceSets.main.get().output)

    manifest {
        attributes("Main-Class" to "org.openjdk.jcstress.Main")
    }

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    // Include dependencies but exclude signature files
    from({
        jcstressRuntimeClasspath.filter { it.exists() }.map {
            if (it.isDirectory) it else zipTree(it)
        }
    }) {
        exclude("META-INF/*.SF")
        exclude("META-INF/*.DSA")
        exclude("META-INF/*.RSA")
        exclude("META-INF/MANIFEST.MF")
    }
}

// JCStress run task
tasks.register<JavaExec>("jcstress") {
    group = "jcstress"
    description = "Runs JCStress concurrency tests"
    dependsOn(jcstressJar)

    mainClass.set("org.openjdk.jcstress.Main")
    classpath = files(jcstressJar.get().archiveFile)

    jvmArgs(
        "--enable-preview",
        "--enable-native-access=ALL-UNNAMED"
    )

    args("-r", "${layout.buildDirectory.get()}/reports/jcstress")
}

// Quick JCStress run
tasks.register<JavaExec>("jcstressQuick") {
    group = "jcstress"
    description = "Runs JCStress with minimal iterations (for development)"
    dependsOn(jcstressJar)

    mainClass.set("org.openjdk.jcstress.Main")
    classpath = files(jcstressJar.get().archiveFile)

    jvmArgs("--enable-preview", "--enable-native-access=ALL-UNNAMED")
    args("-m", "quick", "-r", "${layout.buildDirectory.get()}/reports/jcstress-quick")
}
