package com.bohdanhub.searchapp.ui.component.card

import com.bohdanhub.searchapp.domain.data.root.SearchRequest
import com.bohdanhub.searchapp.domain.data.root.SearchStatus

data class SearchCardData(
    val request: SearchRequest,
    val status: SearchStatus,
    val latestUrl: String,
    val processedUrlsCount: Int,
    val totalUrlsCount: Int,
    val progress: Float,
    val textEntriesCount: Int,
)