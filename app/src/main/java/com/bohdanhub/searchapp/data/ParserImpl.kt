package com.bohdanhub.searchapp.data

import com.bohdanhub.searchapp.domain.Parser
import com.bohdanhub.searchapp.domain.data.ParseResult
import com.bohdanhub.searchapp.util.countEntries
import com.bohdanhub.searchapp.util.extractUrls
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ParserImpl @Inject constructor() : Parser {
    override suspend fun parse(textForSearch: String, originText: String): ParseResult =
        withContext(Dispatchers.Default) {
            ParseResult(
                foundedTextEntries = originText.countEntries(textForSearch),
                foundedUrls = originText.extractUrls()
            )
        }
}