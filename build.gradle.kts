plugins {
    kotlin("jvm") version "2.3.0"
    id("com.gradleup.shadow") version "8.3.0"
    id("xyz.jpenilla.run-paper") version "2.3.1"
}

group = "net.minepact"
version = "0.1-DEV"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
    maven("https://oss.sonatype.org/content/repositories/releases/") {
        name = "sonatype-oss-releases"
    }
    maven("https://oss.sonatype.org/content/repositories/snapshots/") {
        name = "sonatype-oss-snapshots"
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation(kotlin("reflect"))

    implementation("org.jetbrains.kotlin:kotlin-scripting-common")
    implementation("org.jetbrains.kotlin:kotlin-scripting-jvm")
    implementation("org.jetbrains.kotlin:kotlin-scripting-jvm-host")
    implementation("org.jetbrains.kotlin:kotlin-scripting-dependencies")
    implementation("org.jetbrains.kotlin:kotlin-scripting-dependencies-maven")
    implementation("org.jetbrains.kotlin:kotlin-scripting-compiler-embeddable:2.3.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")

    implementation("com.google.inject:guice:4.2.3")
    implementation("javax.inject:javax.inject:1")
}

tasks {
    runServer {
        minecraftVersion("1.21")
    }

    shadowJar {
        // relocate("kotlin.reflect", "net.minepact.libs.kotlin.reflect")

        relocate(
            "org.jetbrains.kotlin.scripting",
            "net.minepact.libs.kotlin.scripting"
        )

        relocate(
            "org.jetbrains.kotlinx.coroutines",
            "net.minepact.libs.kotlinx.coroutines"
        )

        relocate(
            "org.jetbrains.kotlin.cli",
            "net.minepact.libs.kotlin.cli"
        )

        mergeServiceFiles()
        // relocate(
        //     "com.google.inject",
        //     "net.minepact.libs.guice"
        // )
    }
}

val targetJavaVersion = 21
kotlin {
    jvmToolchain(targetJavaVersion)
}

tasks.build {
    dependsOn("shadowJar")
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("paper-plugin.yml") {
        expand(props)
    }
}
