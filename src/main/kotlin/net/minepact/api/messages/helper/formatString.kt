package net.minepact.api.messages.helper

fun formatString(vararg args: String): String {
    args.forEach {
        if (args[args.size-1] != it) it.plus("\n")
    }
    return args.joinToString("")
}