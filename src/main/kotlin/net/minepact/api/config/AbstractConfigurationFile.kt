package net.minepact.api.config

abstract class AbstractConfigurationFile : ConfigurationFile {
    open fun <T> persisting(initial: T) = PersistingProperty(initial)
}