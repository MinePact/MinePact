package net.minepact.api.config.experimental

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD, AnnotationTarget.CLASS)
annotation class Comment(
    val value: String
)
