package com.bohdanhub.searchapp.domain.data

data class ChildSearchResult(
    val request: ChildSearchRequest,
    val parseResult: Result<ParseResult>,
)
