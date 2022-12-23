package com.bohdanhub.searchapp.domain

import com.bohdanhub.searchapp.domain.data.ParseResult

interface Parser {
    suspend fun parse(textForSearch: String, originText: String): ParseResult
}