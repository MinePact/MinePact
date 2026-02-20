package net.minepact.api.command.arguments

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