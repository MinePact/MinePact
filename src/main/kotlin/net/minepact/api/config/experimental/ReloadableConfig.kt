package net.minepact.api.config.experimental

interface ReloadableConfig<T : ConfigurationFile> {
    fun onReload(old: T)
}