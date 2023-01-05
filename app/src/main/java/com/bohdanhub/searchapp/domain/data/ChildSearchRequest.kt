package com.bohdanhub.searchapp.domain.data

data class ChildSearchRequest(
    val url: String,
    val parentId: Long,
    val id: Long,
    val deep: Int,
    val priority: List<Int>,
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
            url: String,
            parentId: Long,
            results: List<ChildSearchResult>
        ): List<Int> {
            val priority = mutableListOf<Int>()
            var parentId: Long? = parentId
            var url: String? = url
            while (parentId != null) {
                val parent = results.find { it.request.id == parentId }
                val i =
                    (parent?.parseResult as? Result.Success)?.result?.foundedUrls?.indexOf(url)
                if (i != null && i >= 0) {
                    priority.add(i)
                }
                parentId = parent?.request?.parentId
                url = parent?.request?.url
            }
            priority.add(0)
            return priority.reversed()
        }
    }
}
