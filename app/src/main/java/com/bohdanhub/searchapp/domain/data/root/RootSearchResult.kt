package com.bohdanhub.searchapp.domain.data.root

data class RootSearchResult(
    val request: RootSearchRequest,
    val textEntries: Int,
    val foundedUrls: List<String>,
    val processedUrls: List<String>,
    val status: RootSearchStatus,
    val searchTree: Node? = null
) {
    fun getProgress(): Float = if (foundedUrls.isNotEmpty() && processedUrls.isNotEmpty())
        processedUrls.size.toFloat() / foundedUrls.size.toFloat()
    else 0f
}
