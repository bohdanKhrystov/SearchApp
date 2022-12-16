package com.bohdanhub.searchapp.domain.data

data class RootSearchResult(
    val request: RootSearchRequest,
    val totalTextEntries: Int,
    val foundedUrls: List<String>,
    val processedUrls: List<String>,
    val status: SearchStatus,
    val progress: Float = foundedUrls.size.toFloat() / processedUrls.size.toFloat()
)
