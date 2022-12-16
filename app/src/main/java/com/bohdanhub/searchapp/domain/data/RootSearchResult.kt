package com.bohdanhub.searchapp.domain.data

data class RootSearchResult(
    val request: RootSearchRequest,
    val totalTextEntries: Int,
    val foundedUrls: List<String>,
    val processedUrls: List<String>,
    val status: SearchStatus,
) {
    fun getProgress(): Float = if (foundedUrls.isNotEmpty() && processedUrls.isNotEmpty())
        processedUrls.size.toFloat() / foundedUrls.size.toFloat()
    else 0f
}
