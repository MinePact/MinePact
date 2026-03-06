package net.minepact.api.config.experimental

abstract class AbstractConfigurationFile : ConfigurationFile {
    open fun <T> persisting(initial: T) = PersistingProperty(initial)
}