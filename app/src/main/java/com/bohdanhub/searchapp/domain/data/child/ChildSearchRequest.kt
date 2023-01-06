package com.bohdanhub.searchapp.domain.data.child

import com.bohdanhub.searchapp.domain.data.common.Result

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

    companion object {
        fun calculatePriority(
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

        fun empty(): ChildSearchRequest {
            return ChildSearchRequest("", -1L, -1L, -1, listOf(), ChildRequestStatus.New)
        }
    }
}
