package net.minepact.api.scripts.engine


import net.minepact.Main
import java.io.File
import java.nio.file.Files

/**
 * Generates the scripts IDE companion project next to the server on first run.
 * Call from ScriptManager.loadAll() before loading any scripts.
 *
 * Generates: <serverRoot>/minepact-scripts-ide/
 *   ├── build.gradle.kts       (pre-wired to the plugin JAR)
 *   ├── settings.gradle.kts
 *   ├── .gitignore
 *   └── scripts/               (symlink target — place your .kts files here)
 */
class ScriptProjectProvisioner() {
    private val serverRoot = Main.instance.server.worldContainer.toPath().toAbsolutePath().normalize()
    private val projectRoot = serverRoot.resolve("minepact-scripts-ide")
    private val apiDir = projectRoot.resolve(".minepact").resolve("api")

    fun ensureProjectExists() {
        if (!Files.exists(projectRoot)) {
            Files.createDirectories(projectRoot.resolve("scripts"))
            writeSettings()
            writeGitignore()
            writeReadme()
            Main.instance.logger.info("[ScriptEngine] Generated IDE companion project at $projectRoot")
            Main.instance.logger.info("[ScriptEngine] Open it in IntelliJ for full script autocomplete.")
        }

        ensureApiJar()
        writeBuildGradle()
    }

    // -------------------------------------------------------------------------

    private fun ensureApiJar() {
        Files.createDirectories(apiDir)
        val target = apiDir.resolve("MinePact.jar").toFile()

        val pluginJar = Main.instance.javaClass.protectionDomain.codeSource.location.toURI().let { File(it) }
        if (pluginJar.isFile) {
            pluginJar.copyTo(target, overwrite = true)
        }
    }

    private fun writeBuildGradle() {
        val scriptsPath = Main.instance.dataFolder.toPath().resolve("scripts")
            .toAbsolutePath().normalize().toString().replace('\\', '/')

        projectRoot.resolve("build.gradle.kts").toFile().writeText("""
            plugins {
                kotlin("jvm") version "2.3.0"
            }

            repositories {
                mavenCentral()
                maven("https://repo.papermc.io/repository/maven-public/")
            }

            dependencies {
                // Plugin JAR — auto-updated by the plugin on every server start
                implementation(files(".minepact/api/MinePact.jar"))

                // Paper API
                implementation("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")

                // Kotlin scripting support (required for IDE to recognise @KotlinScript)
                implementation(kotlin("scripting-common"))
                implementation(kotlin("scripting-jvm"))
                implementation(kotlin("scripting-jvm-host"))
                implementation(kotlin("scripting-compiler-embeddable"))
                implementation(kotlin("stdlib"))
            }

            // Deploy scripts to the server scripts folder
            tasks.register<Copy>("deploy") {
                from("scripts")
                into("$scriptsPath")
                include("**/*.kts")
            }
        """.trimIndent())
    }

    private fun writeSettings() {
        projectRoot.resolve("settings.gradle.kts").toFile().writeText("""
            rootProject.name = "minepact-scripts-ide"
        """.trimIndent())
    }

    private fun writeGitignore() {
        projectRoot.resolve(".gitignore").toFile().writeText("""
            .gradle/
            build/
            .idea/
            *.iml
            .minepact/
        """.trimIndent())
    }

    private fun writeReadme() {
        projectRoot.resolve("README.md").toFile().writeText("""
            # MinePact Scripts IDE Project

            Open this folder in IntelliJ IDEA as a Gradle project (Java 21).

            Put your `.minepact.kts` and `bootstrap.minepact.kts` files in `scripts/`.

            ## Workflow
            1. Edit scripts here with full autocomplete
            2. Run `./gradlew deploy` to copy scripts to the server
            3. Run `/script reload` in game

            ## Autocomplete
            The `.minepact/api/MinePact.jar` file is automatically updated every time
            the server starts, so your autocomplete always reflects the latest API.
            After a plugin update, just do File → Reload Gradle Project in IntelliJ.
        """.trimIndent())
    }
}