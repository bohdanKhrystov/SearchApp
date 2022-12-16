package com.bohdanhub.searchapp.domain.data

data class RootSearchRequest(
    val textForSearch: String,
    val url: String,
    val maxUrls: Int = 20,
)
