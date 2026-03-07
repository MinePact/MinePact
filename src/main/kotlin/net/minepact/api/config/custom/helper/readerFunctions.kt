package net.minepact.api.config.custom.helper

import net.minepact.api.config.custom.interfaces.FileReader
import kotlin.reflect.typeOf

inline fun <reified T> FileReader.get(path: String): T = get(path, typeOf<T>())
inline fun <reified T> FileReader.getOrDefault(path: String, default: T): T = if (contains(path)) get<T>(path) else default
inline fun <reified T> FileReader.getOrNull(path: String): T? = if (contains(path)) get<T>(path) else null