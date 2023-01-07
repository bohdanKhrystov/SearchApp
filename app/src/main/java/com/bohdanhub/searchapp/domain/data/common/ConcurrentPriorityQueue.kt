package com.bohdanhub.searchapp.domain.data.common

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.PriorityQueue

class ConcurrentPriorityQueue<T> {

    private val queue = PriorityQueue<T>()
    private val mutex = Mutex()

    suspend fun poll(): T? = mutex.withLock {
        queue.poll()
    }

    suspend fun add(item: T) = mutex.withLock {
        queue.add(item)
    }

    suspend fun clear() = mutex.withLock {
        queue.clear()
    }

    suspend fun peek(): T? = mutex.withLock {
        queue.peek()
    }

    suspend fun size() = mutex.withLock {
        queue.size
    }
}