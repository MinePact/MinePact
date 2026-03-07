package net.minepact.api.config.custom.exception

class ConfigParseException(message: String, line: Int = -1, cause: Throwable? = null)
    : ConfigException(if (line >= 0) "Parse error on line $line: $message" else "Parse error: $message", cause)
