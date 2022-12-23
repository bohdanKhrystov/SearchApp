package com.bohdanhub.searchapp.ui.component.card

import com.bohdanhub.searchapp.domain.data.RootSearchRequest
import com.bohdanhub.searchapp.domain.data.RootSearchStatus

data class SearchCardData(
    val request: RootSearchRequest,
    val status: RootSearchStatus,
    val latestUrl: String,
    val processedUrlsCount: Int,
    val totalUrlsCount: Int,
    val progress: Float,
    val textEntriesCount: Int,
)