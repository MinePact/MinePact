package net.minepact.api.event

class EventContext<E> (val event: E) {
    var cancelled: Boolean = false
}