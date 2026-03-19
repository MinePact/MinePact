package net.minepact.api.discord

import net.minepact.api.misc.Constants

object Webhooks {
    val UPDATES_WEBHOOK = Webhook(
        username = "Server Updates",
        avatarUrl = Constants.WEBHOOK_AVATAR_URL
    )
    val LOGGING_WEBHOOK = Webhook(
        username = "Logger",
        webhookUrl = "https://discord.com/api/webhooks/1472953480300990475/uWLQTkM7vykCRfUKAT1NYogeNLMFswZdEruu5mg-L3lskiLcroBMo_uqLo2xRPahGe65",
        avatarUrl = Constants.WEBHOOK_AVATAR_URL
    )
    val PUNISHMENTS_WEBHOOK = Webhook(
        username = "Punishments",
        webhookUrl = "https://discord.com/api/webhooks/1473475316713525283/eXnVM7gHRbw75g_5XtDVcWRqjXqr09uA2fazva72FpiG15kPIo0ZwPAaM6Ip4Isw4NXu",
        avatarUrl = Constants.WEBHOOK_AVATAR_URL
    )
}