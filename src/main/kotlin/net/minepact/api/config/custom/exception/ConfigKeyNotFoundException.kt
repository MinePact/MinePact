package net.minepact.api.config.custom.exception

class ConfigKeyNotFoundException(val path: String) : ConfigException("No config value found at path: '$path'")