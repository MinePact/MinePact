package net.minepact.core.global.commands.staff.punishment.helper

import net.minepact.Main
import net.minepact.api.config.custom.ConfigManager
import net.minepact.api.config.custom.helper.MinePactConfigType
import net.minepact.api.config.custom.helper.get
import net.minepact.api.punishment.modifier.AnnouncementModifier
import net.minepact.api.punishment.modifier.PunishmentModifier

fun resolveAnnouncementModifier(modifiers: List<PunishmentModifier>): AnnouncementModifier {
    val announcementModifiers = modifiers.filterIsInstance<AnnouncementModifier>()

    return when {
        announcementModifiers.contains(AnnouncementModifier.SILENT) && announcementModifiers.contains(AnnouncementModifier.PUBLIC) ->
            AnnouncementModifier.valueOf(ConfigManager.file<MinePactConfigType>("config.mpc").reader.get<String>("punishments.default_announcement"))
        announcementModifiers.contains(AnnouncementModifier.SILENT) -> AnnouncementModifier.SILENT
        announcementModifiers.contains(AnnouncementModifier.PUBLIC) -> AnnouncementModifier.PUBLIC
        else -> AnnouncementModifier.valueOf(ConfigManager.file<MinePactConfigType>("config.mpc").reader.get<String>("punishments.default_announcement"))
    }
}