package net.minepact.api.scripts.annotations

@Target(AnnotationTarget.FILE)
@Retention(AnnotationRetention.RUNTIME)
annotation class DependsOn(vararg val scripts: String)
