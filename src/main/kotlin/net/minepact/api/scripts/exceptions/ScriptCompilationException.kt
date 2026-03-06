package net.minepact.api.scripts.exceptions

class ScriptCompilationException(scriptName: String, errors: String) : Exception("Failed to compile '$scriptName':\n$errors")