package net.minepact.api.command.arguments

/**
 * Parses a string input into an Argument of the expected type, validating against any specified range.
 * @param input The raw string input to parse.
 * @param expected The ExpectedArgument defining the expected type and range for the argument.
 *
 * @return An Argument containing the parsed value if successful, or null if parsing fails or the value is out of range.
 *
 * @see Argument
 * @see ExpectedArgument
 * @see ArgumentRange
 * @see ArgumentInputType
 *
 * @author dankenyon - 22/02/26
 */
fun parseArgument(
    input: String,
    expected: ExpectedArgument
): Argument<*>? {
    val parsed = expected.inputType.parse(input) ?: return null

    when (val range = expected.range) {
        is IntegerArgumentRange -> {
            val v = parsed as Int
            if (v < range.min || v > range.max) return null
        }
        is DoubleArgumentRange -> {
            val v = parsed as Double
            if (v < range.min || v > range.max) return null
        }
        is FloatArgumentRange -> {
            val v = parsed as Float
            if (v < range.min || v > range.max) return null
        }
        is EmptyArgumentRange -> {}
    }

    return Argument(
        raw = input,
        value = parsed,
        type = expected.inputType
    )
}