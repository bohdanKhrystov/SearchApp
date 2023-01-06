package com.bohdanhub.searchapp.domain.data.root

import com.bohdanhub.searchapp.domain.data.child.ChildSearchRequest

data class RootSearchResult(
    val request: RootSearchRequest,
    val textEntries: Int,
    val foundedUrls: List<String>,
    val processedUrls: List<String>,
    val status: RootSearchStatus,
    val childRequests: Map<Long, List<ChildSearchRequest>>? = null
) {
    fun getProgress(): Float = if (foundedUrls.isNotEmpty() && processedUrls.isNotEmpty())
        processedUrls.size.toFloat() / foundedUrls.size.toFloat()
    else 0f
}
