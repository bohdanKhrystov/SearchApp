package com.bohdanhub.searchapp.domain.data.child

import com.bohdanhub.searchapp.domain.data.parse.ParseResult
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

data class ChildSearchResult(
    val parseResult: ParseResult,
    val childIds: List<Long>
){
    object Factory {

        private val mutex = Mutex()
        private var requestId = 0L

        suspend fun createResult(parseResult: ParseResult): ChildSearchResult {
            val childIds = parseResult.foundedUrls.map { generateId() }
            return ChildSearchResult(parseResult, childIds)
        }

        suspend fun toDefault() = mutex.withLock {
            requestId = 0
        }

        private suspend fun generateId(): Long = mutex.withLock {
            requestId += 1
            return requestId
        }
    }
}
