package net.minepact.api.queue

abstract class BaseQueueImpl {
    val queue: MutableList<QueueObject> = mutableListOf()

    fun add(entry: QueueObject) {
        queue.add(entry)
    }
    fun remove(entry: QueueObject) {
        queue.remove(entry)
    }
    fun entries(): List<QueueObject> {
        return queue.toList()
    }

    abstract fun next(): QueueObject?
}