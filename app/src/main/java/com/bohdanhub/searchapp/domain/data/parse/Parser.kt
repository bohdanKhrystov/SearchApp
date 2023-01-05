package com.bohdanhub.searchapp.domain.data.parse

import com.bohdanhub.searchapp.domain.data.parse.ParseResult

interface Parser {
    suspend fun parse(textForSearch: String, originText: String): ParseResult
}