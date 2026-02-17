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
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-reflect:2.3.10")
    implementation("com.zaxxer:HikariCP:5.1.0")
    implementation("org.mariadb.jdbc:mariadb-java-client:3.4.1")
    // JSON support for menu serialization
    implementation("com.google.code.gson:gson:2.10.1")
}

tasks {
  runServer {
    minecraftVersion("1.21")
  }

    shadowJar {
        destinationDirectory = file("C:\\Users\\danhk\\Desktop\\servers\\mp\\plugins")
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
