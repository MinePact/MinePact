package net.minepact.api.config

abstract class AbstractConfigurationFile : ConfigurationFile {
    protected fun <T> persisting(initial: T) = PersistingProperty(initial)
}