package com.bohdanhub.searchapp.domain.data.common

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class ConcurrentHashMap<K, V> {

    private val mutex = Mutex()
    private val map = mutableMapOf<K, V>()

    suspend fun toMap(): Map<K, V> = mutex.withLock { map.toMap() }

    suspend fun entries(): Set<Map.Entry<K, V>> = mutex.withLock {
        map.entries
    }

    suspend fun keys(): Set<K> = mutex.withLock {
        map.keys
    }

    suspend fun size(): Int = mutex.withLock {
        map.size
    }

    suspend fun values(): Collection<V> = mutex.withLock {
        map.values
    }

    suspend fun containsKey(key: K): Boolean = mutex.withLock {
        map.containsKey(key)
    }

    suspend fun containsValue(value: V): Boolean = mutex.withLock {
        map.containsValue(value)
    }

    suspend fun get(key: K): V? = mutex.withLock {
        map[key]
    }

    suspend fun isEmpty(): Boolean = mutex.withLock {
        map.isEmpty()
    }

    suspend fun clear() = mutex.withLock {
        map.clear()
    }

    suspend fun put(key: K, value: V): V? = mutex.withLock {
        map.put(key, value)
    }

    suspend fun putAll(from: Map<out K, V>) = mutex.withLock {
        map.putAll(from)
    }

    suspend fun remove(key: K): V? = mutex.withLock {
       map.remove(key)
    }
}