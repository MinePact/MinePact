plugins {
    kotlin("jvm") version "2.3.0"
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.19"
    id("com.gradleup.shadow") version "8.3.0"
    id("xyz.jpenilla.run-paper") version "2.3.1"
}

val kotlinVersion = "2.3.0"

group = "net.minepact"
version = "0.1-DEV"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
}

dependencies {
    paperweight.paperDevBundle("1.21.11-R0.1-SNAPSHOT")

    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
    compileOnly("net.luckperms:api:5.5")

    implementation(kotlin("stdlib"))
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")

    implementation("org.jetbrains.kotlin:kotlin-scripting-common:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-scripting-jvm:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-scripting-jvm-host:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-scripting-compiler-embeddable:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-compiler-embeddable:$kotlinVersion")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-api:2.20.0")
    implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-core:2.20.0")

    implementation("com.zaxxer:HikariCP:5.1.0")
    implementation("org.mariadb.jdbc:mariadb-java-client:3.4.1")
    implementation("com.google.code.gson:gson:2.10.1")
}

val targetJavaVersion = 21
kotlin {
    jvmToolchain(targetJavaVersion)
}

tasks {
    build {
        dependsOn(shadowJar)
    }

    runServer {
        minecraftVersion("1.21")
    }

    processResources {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        filteringCharset = "UTF-8"

        val props = mapOf("version" to version)
        inputs.properties(props)

        filesMatching("paper-plugin.yml") {
            expand(props)
        }
    }

    shadowJar {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        archiveClassifier.set("")
        destinationDirectory = layout.buildDirectory.dir("libs")

        minimize {
            exclude(dependency("org.jetbrains.kotlin:kotlin-compiler-embeddable"))
            exclude(dependency("org.jetbrains.kotlin:kotlin-scripting-compiler-embeddable"))
            exclude(dependency("org.mariadb.jdbc:mariadb-java-client"))
            exclude(dependency("com.zaxxer:HikariCP"))
        }

        filesMatching("META-INF/kotlin/**") {}
    }

    register<Task>("deployToServer") {
        dependsOn(shadowJar)
        doLast {
            val serverPluginsDir = file("C:\\Users\\danhk\\Desktop\\servers\\mp\\plugins")
            val jarName = shadowJar.get().archiveFileName.get()

            val existing = File(serverPluginsDir, jarName)
            if (existing.exists()) existing.delete()

            copy {
                from(shadowJar.get().archiveFile)
                into(serverPluginsDir)
            }
        }
    }

    build {
        dependsOn("deployToServer")
    }
}