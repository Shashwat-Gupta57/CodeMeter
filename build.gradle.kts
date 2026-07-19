plugins {
    java
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("org.graalvm.buildtools.native") version "0.10.2"
    id("org.cyclonedx.bom") version "1.8.2"
}

group = "dev.codemeter"
version = "2.5.0"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // CLI Framework
    implementation("info.picocli:picocli:4.7.6")
    annotationProcessor("info.picocli:picocli-codegen:4.7.6")

    // Terminal UI dependencies removed in v2.1
    implementation("net.java.dev.jna:jna:5.14.0")
    implementation("net.java.dev.jna:jna-platform:5.14.0")
    compileOnly("org.graalvm.nativeimage:svm:23.1.2") // For GraalVM substitutions

    // JSON processing
    implementation("com.google.code.gson:gson:2.11.0")

    // TOML processing
    implementation("com.moandjiezana.toml:toml4j:0.7.2")

    // PDF export
    implementation("org.apache.pdfbox:pdfbox:3.0.3")

    // SVG export
    implementation("org.apache.xmlgraphics:batik-svggen:1.17")
    implementation("org.apache.xmlgraphics:batik-dom:1.17")

    // Image generation
    implementation("org.apache.xmlgraphics:batik-codec:1.17")

    // Testing
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.3")
    testImplementation("org.mockito:mockito-core:5.12.0")
    testImplementation("org.assertj:assertj-core:3.26.0")
}

application {
    mainClass.set("dev.codemeter.CodeMeter")
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    manifest {
        attributes(
            "Main-Class" to "dev.codemeter.CodeMeter",
            "Implementation-Title" to "CodeMeter",
            "Implementation-Version" to project.version
        )
    }
}

tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
    archiveBaseName.set("codemeter")
    archiveClassifier.set("")
    archiveVersion.set("")
    mergeServiceFiles()
}

graalvmNative {
    binaries {
        named("main") {
            imageName.set("codemeter")
            fallback.set(false)
            buildArgs.add("-H:IncludeResources=com/sun/jna/win32-x86-64/jnidispatch\\.dll")
        }
    }
    metadataRepository {
        enabled.set(true)
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-Aproject=${project.group}/${project.name}")
}

tasks.register("createLaunchScripts") {
    dependsOn("shadowJar")
    doLast {
        val binDir = layout.buildDirectory.dir("install/bin").get().asFile
        binDir.mkdirs()
        val jarFile = tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar").get().archiveFile.get().asFile

        // Create launch script for Unix
        file("${binDir}/codemeter").writeText("""
            #!/usr/bin/env bash
            exec java -jar "${jarFile.absolutePath}" "${'$'}@"
        """.trimIndent())
        file("${binDir}/codemeter").setExecutable(true)

        // Create launch script for Windows
        file("${binDir}/codemeter.bat").writeText("""
            @echo off
            java -jar "${jarFile.absolutePath}" %*
        """.trimIndent())
    }
}
