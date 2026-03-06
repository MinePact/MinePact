package net.minepact.api.scripts.exceptions

class ScriptEvaluationException(errors: String) : Exception("Script evaluation failed:\n$errors")