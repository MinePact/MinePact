package net.minepact.api.messages

enum class MessageColour(
    val tag: String,
    val legacy: String
) {
    BLACK("black", "§0"),
    DARK_BLUE("dark_blue", "§1"),
    DARK_GREEN("dark_green", "§2"),
    DARK_AQUA("dark_aqua", "§3"),
    DARK_RED("dark_red", "§4"),
    DARK_PURPLE("dark_purple", "§5"),
    GOLD("gold", "§6"),
    GRAY("gray", "§7"),
    DARK_GRAY("dark_gray", "§8"),
    BLUE("blue", "§9"),
    GREEN("green", "§a"),
    AQUA("aqua", "§b"),
    RED("red", "§c"),
    LIGHT_PURPLE("light_purple", "§d"),
    YELLOW("yellow", "§e"),
    WHITE("white", "§f"),

    OBFUSCATED("obfuscated", "§k"),
    BOLD("bold", "§l"),
    STRIKETHROUGH("strikethrough", "§m"),
    UNDERLINE("underline", "§n"),
    ITALIC("italic", "§o"),
    RESET("reset", "§r");

    companion object {
        private val TAG_MAP: Map<String, String> = entries.associate { it.tag.lowercase() to it.legacy }

        fun asMap(): Map<String, String> = TAG_MAP
        fun fromTag(tag: String): MessageColour? {
            return entries.firstOrNull { it.tag.equals(tag, ignoreCase = true) }
        }
    }
}