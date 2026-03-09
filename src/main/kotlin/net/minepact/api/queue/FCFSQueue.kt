package net.minepact.api.queue

class FCFSQueue : BaseQueueImpl() {
    override fun next(): QueueObject {
        if (queue.isEmpty()) throw NoSuchElementException("The queue is empty.")
        return queue.first()
    }
}