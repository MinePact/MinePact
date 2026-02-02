package net.minepact.api.command.arguments

enum class ArgumentInputType {
    INTEGER,
    STRING,
    BOOLEAN,
    DOUBLE,
    FLOAT;

    fun parse(input: String): Any? = try {
        when (this) {
            STRING -> input
            INTEGER -> input.toInt()
            DOUBLE -> input.toDouble()
            FLOAT -> input.toFloat()
            BOOLEAN -> input.equals("true", true) || input.equals("false", true)
        }
    } catch (e: Exception) {
        null
    }
}