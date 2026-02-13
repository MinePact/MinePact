package net.minepact.api.config

interface ReloadableConfig<T : ConfigurationFile> {
    fun onReload(old: T)
}