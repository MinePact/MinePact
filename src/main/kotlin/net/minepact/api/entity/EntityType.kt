package net.minepact.api.entity

interface EntityType {
    val key: String
    val displayName: String

    fun create(): CustomEntity
}