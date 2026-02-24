package net.minepact.api.command.arguments

/**
 * Represents the type of input expected for a command argument.
 * @author dankenyon - 22/02/26
 */
enum class ArgumentInputType {
    INTEGER,
    STRING,
    BOOLEAN,
    DOUBLE,
    FLOAT,
    LONG;

    fun parse(input: String): Any? = try {
        when (this) {
            STRING -> input
            INTEGER -> input.toInt()
            DOUBLE -> input.toDouble()
            FLOAT -> input.toFloat()
            LONG -> input.toLong()
            BOOLEAN -> input.equals("true", true) || input.equals("false", true)
        }
    } catch (e: Exception) {
        null
    }
}