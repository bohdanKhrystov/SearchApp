package com.bohdanhub.searchapp.domain.data.child

import com.bohdanhub.searchapp.domain.data.common.ConcurrentHashMap
import com.bohdanhub.searchapp.domain.data.common.Result
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

data class ChildSearchRequest(
    val url: String,
    val parentId: Long,
    val id: Long,
    val deep: Int,
    val priority: List<Int>,
    val status: ChildRequestStatus,
) : Comparable<ChildSearchRequest> {

    override fun compareTo(other: ChildSearchRequest): Int {
        if (priority.size > other.priority.size) return 1
        if (priority.size < other.priority.size) return -1
        for (i in priority.indices) {
            if (priority[i] > other.priority[i]) return 1
            if (priority[i] < other.priority[i]) return -1
        }
        return 0
    }

    class Factory(
        private val requestsByParentId: ConcurrentHashMap<Long, List<ChildSearchRequest>>,
    ) {

        fun createRootRequest(url: String): ChildSearchRequest {
            return ChildSearchRequest(
                url = url,
                parentId = -1,
                id = 0,
                deep = 0,
                priority = listOf(0),
                status = ChildRequestStatus.Queued
            )
        }

        suspend fun createRequests(
            parent: ChildSearchRequest?
        ): List<ChildSearchRequest> {
            val result = (parent?.status as? ChildRequestStatus.Completed)?.result
            if (result !is Result.Success) return listOf()
            val foundedUrls = result.data.parseResult.foundedUrls
            val nextDeep = parent.deep + 1
            return foundedUrls.mapIndexed { index, url ->
                val id = result.data.childIds[index]
                ChildSearchRequest(
                    url = url,
                    id = id,
                    deep = nextDeep,
                    parentId = parent.id,
                    priority = calculatePriority(
                        id = id,
                        parentId = parent.id,
                        completedRequests = requestsByParentId.with { map ->
                            map.values
                                .flatten()
                                .filter { it.status is ChildRequestStatus.Completed }
                        }
                    ),
                    status = ChildRequestStatus.Queued,
                )
            }
        }

        private fun calculatePriority(
            id: Long,
            parentId: Long,
            completedRequests: List<ChildSearchRequest>
        ): List<Int> {
            val priority = mutableListOf<Int>()
            var parentId: Long? = parentId
            var id: Long? = id
            while (parentId != null) {
                val parent = completedRequests.find { it.id == parentId }
                val childIndex =
                    ((parent?.status as? ChildRequestStatus.Completed)?.result as? Result.Success)?.data?.childIds?.indexOf(
                        id
                    )
                if (childIndex != null && childIndex >= 0) {
                    priority.add(childIndex)
                }
                id = parent?.id
                parentId = parent?.parentId
            }
            priority.add(0)
            return priority.reversed()
        }
    }
}
