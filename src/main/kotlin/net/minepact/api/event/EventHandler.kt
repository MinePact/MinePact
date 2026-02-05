package net.minepact.api.event

import java.lang.reflect.ParameterizedType

interface EventHandler<E : Any> {
    val priority: Int get() = 0
    val filter: ((E) -> Boolean)? get() = null

    fun handle(context: EventContext<E>)
}

abstract class SimpleEventHandler<E : Any>(
    override val priority: Int = 0
) : EventHandler<E> {
    @Suppress("UNCHECKED_CAST")
    val eventClass: Class<E> by lazy {
        (this::class.java.genericInterfaces
            .mapNotNull { it as? ParameterizedType }
            .firstOrNull { EventHandler::class.java.isAssignableFrom((it.rawType as Class<*>)) }
            ?.actualTypeArguments?.get(0) as? Class<E>)
            ?: ((this::class.java.genericSuperclass as? ParameterizedType)
                ?.actualTypeArguments?.get(0) as Class<E>)
            ?: throw IllegalStateException("Cannot infer event class for handler ${this::class.java.name}")
    }
}