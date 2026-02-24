package net.minepact.api.command

@Deprecated("")
enum class CommandFailureReason {
    INSUFFICIENT_PERMISSIONS,
    INVALID_ARGUMENTS,
    COOLDOWN_ACTIVE,
    UNKNOWN_ERROR;
}