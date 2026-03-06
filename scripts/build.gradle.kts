plugins {
    kotlin("jvm") version "2.3.0"
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    // Reference the main project's output JAR directly — always up to date
    implementation(
        files("C:/Users/danhk/Desktop/servers/mp/plugins/MinePact-0.1-DEV.jar")
    )
    implementation("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")

    implementation(kotlin("scripting-common"))
    implementation(kotlin("scripting-jvm"))
    implementation(kotlin("scripting-jvm-host"))
    implementation(kotlin("scripting-compiler-embeddable"))
    implementation(kotlin("stdlib"))
}

// Make sure the main JAR is built before this module tries to use it
tasks.named("compileKotlin") {
    dependsOn(":shadowJar")
}

tasks.register<Copy>("deploy") {
    from("scripts")
    into("C:/Users/danhk/Desktop/servers/mp/plugins/MinePact/scripts")
    include("**/*.kts")
}