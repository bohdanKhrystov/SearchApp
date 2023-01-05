package com.bohdanhub.searchapp.domain.data.parse

data class ParseResult(
    val foundedTextEntries: Int,
    val foundedUrls: List<String>,
)
