package net.minepact.api.scripts

import net.minepact.Main
import net.minepact.api.scripts.bootstrap.BootstrapDSL
import net.minepact.api.scripts.bootstrap.BootstrapLoader
import net.minepact.api.scripts.engine.ScriptProjectProvisioner
import net.minepact.api.scripts.registries.ListenerRegistry
import net.minepact.api.scripts.registries.ScriptServiceRegistry
import java.io.File
import java.util.logging.Logger
import kotlin.reflect.KClass

class ScriptManager(
    private val plugin: Main = Main.instance,
    scriptsDir: File  = File(plugin.dataFolder, "scripts"),
    cacheDir: File = File(plugin.dataFolder, ".script-cache")
) {
    private val logger: Logger = plugin.logger
    val scriptsDir: File = scriptsDir.also { it.mkdirs() }

    private val compiler = ScriptCompiler(logger, cacheDir)
    private val loader = ScriptLoader(compiler, logger)
    private val bootstrapLoader = BootstrapLoader(logger)
    private val resolver = DependencyResolver()
    private val registry = ScriptServiceRegistry()
    private val listenerRegistry = ListenerRegistry()
    private val instances = mutableMapOf<String, Any?>()

    suspend fun loadAll() {
        logger.info("[ScriptEngine] ══════════════════════════════════")
        logger.info("[ScriptEngine] MinePact Script Engine starting...")

        ScriptProjectProvisioner().ensureProjectExists()

        val bootstrapFile = File(scriptsDir, "bootstrap.minepact.kts")
        val bootstrapCfg = if (bootstrapFile.exists()) bootstrapLoader.load(bootstrapFile) else null
        val scriptFiles = loader.discover(scriptsDir)
        val byName = scriptFiles.associateBy { loader.nameOf(it) }
        if (byName.isEmpty()) {
            logger.info("[ScriptEngine] No scripts found in ${scriptsDir.path}")
            return
        }

        val annotationDeps: MutableMap<String, Set<String>> = byName.mapValues { (_, f) -> loader.dependenciesOf(f) }.toMutableMap()
        bootstrapCfg?.explicitDeps?.forEach { (name, deps) -> annotationDeps[name] = deps }
        val loadOrder = try {
            buildLoadOrder(bootstrapCfg, byName.keys, annotationDeps)
        } catch (e: IllegalStateException) {
            logger.severe("[ScriptEngine] Load order resolution failed: ${e.message}")
            return
        }
        logger.info("[ScriptEngine] Load order: ${loadOrder.joinToString(" → ")}")

        var ok = 0; var fail = 0
        for (name in loadOrder) {
            val file = byName[name] ?: continue
            val api  = ScriptAPI(plugin, plugin.server, registry, name, listenerRegistry)
            loader.load(file, api)
                .onSuccess { instance ->
                    instances[name] = instance
                    logger.info("[ScriptEngine] ✓ $name")
                    ok++
                }
                .onFailure { ex ->
                    logger.severe("[ScriptEngine] ✗ $name — ${ex.message}")
                    fail++
                }
        }

        logger.info("[ScriptEngine] Done. $ok loaded, $fail failed.")
        logger.info("[ScriptEngine] Services: [${registry.registeredNames().joinToString()}]")
        logger.info("[ScriptEngine] ══════════════════════════════════")
    }
    suspend fun reload(clearCache: Boolean = false) {
        logger.info("[ScriptEngine] Reloading scripts...")
        unloadAll()
        if (clearCache) compiler.clearCache()
        loadAll()
    }
    fun unloadAll() {
        listenerRegistry.unregisterAll()
        registry.clear()
        instances.clear()
        logger.info("[ScriptEngine] All scripts unloaded.")
    }

    inline fun <reified T : Any> service(): T? = service(T::class)
    fun <T : Any> service(type: KClass<T>): T? = registry[type]
    fun loadedScripts(): Set<String> = instances.keys.toSet()
    fun registeredServices(): List<String> = registry.registeredNames()

    private fun buildLoadOrder(
        bootstrap: BootstrapDSL?,
        allNames: Set<String>,
        depMap: Map<String, Set<String>>
    ): List<String> {
        if (bootstrap == null) {
            return resolver.resolve(depMap)
        }

        val listed = bootstrap.order.toMutableList()
        if (bootstrap.autoDiscover) {
            val unlisted = allNames - listed.toSet()
            if (unlisted.isNotEmpty()) {
                logger.info("[ScriptEngine] Auto-discovering: [${unlisted.sorted().joinToString()}]")
                val unlistedDeps = unlisted.associateWith {
                    depMap[it] ?: emptySet()
                }
                listed.addAll(resolver.resolve(unlistedDeps))
            }
        }

        val finalNames  = listed.filter { it in allNames }
        val finalDepMap = finalNames.associateWith { depMap[it] ?: emptySet() }
        return resolver.resolve(finalDepMap)
    }
}