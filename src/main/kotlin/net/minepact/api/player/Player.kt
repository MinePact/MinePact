package net.minepact.api.player

class Player(
    val data: PlayerData,
    var online: Boolean
) {

    override fun toString(): String {
        return """
            Player[
                data=$data, 
                online=$online
            ]
        """.trimMargin()
    }
}