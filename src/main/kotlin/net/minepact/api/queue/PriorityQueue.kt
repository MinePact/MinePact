package net.minepact.api.queue

class PriorityQueue : BaseQueueImpl() {
    override fun next(): QueueObject {
        queue.sortByDescending { it.priority }
        if (queue.isEmpty()) throw NoSuchElementException("The queue is empty.")

        return queue.first()
    }
}