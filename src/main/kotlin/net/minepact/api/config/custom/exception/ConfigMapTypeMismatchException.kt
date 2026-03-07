package net.minepact.api.config.custom.exception

class ConfigMapTypeMismatchException(
    path: String,
    side: String,
    expected: String,
    actual: String
) : ConfigException("Type mismatch in map at '$path': $side expected $expected, found $actual")