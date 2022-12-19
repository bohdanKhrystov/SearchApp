package com.bohdanhub.searchapp.domain.data

data class ChildSearchRequest(
    val url: String,
    val parentId: Long,
    val id: Long,
    val deep: Int,
) : Comparable<ChildSearchRequest> {

    private val uuid = "${deep}_${parentId}_${id}"

    override fun compareTo(other: ChildSearchRequest): Int {
        return uuid.compareTo(other.uuid)
    }
}
