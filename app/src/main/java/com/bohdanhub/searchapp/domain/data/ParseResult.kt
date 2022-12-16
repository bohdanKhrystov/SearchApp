package com.bohdanhub.searchapp.domain.data

data class ParseResult(
    val foundedTextEntries: Int,
    val foundedUrls: List<String>,
)
