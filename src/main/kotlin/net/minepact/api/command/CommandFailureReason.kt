package net.minepact.api.command

enum class CommandFailureReason {
    INSUFFICIENT_PERMISSIONS,
    INVALID_ARGUMENTS,
    COOLDOWN_ACTIVE,
    UNKNOWN_ERROR;
}