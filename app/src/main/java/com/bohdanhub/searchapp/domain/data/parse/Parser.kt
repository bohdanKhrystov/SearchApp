package com.bohdanhub.searchapp.domain.data.parse

interface Parser {
    suspend fun parse(textForSearch: String, originText: String): ParseResult
}