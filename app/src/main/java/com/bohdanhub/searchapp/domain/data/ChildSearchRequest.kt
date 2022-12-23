package com.bohdanhub.searchapp.domain.data

data class ChildSearchRequest(
    val url: String,
    val parentId: Long,
    val id: Long,
    val deep: Int,
) : Comparable<ChildSearchRequest> {

    override fun compareTo(other: ChildSearchRequest): Int {
        if (deep > other.deep) return 1
        if (deep < other.deep) return -1
        if (parentId > other.parentId) return 1
        if (parentId < other.parentId) return -1
        if (id > other.id) return 1
        if (id < other.id) return -1
        return 0
    }
}
