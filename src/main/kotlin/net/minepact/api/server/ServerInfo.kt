package net.minepact.api.server

import java.util.UUID

data class ServerInfo(
    var uuid: UUID,
    var name: String,
    var type: ServerType,
    var staging: Boolean
)