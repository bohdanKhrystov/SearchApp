package com.bohdanhub.searchapp.domain.data.child

import com.bohdanhub.searchapp.domain.data.parse.ParseResult
import com.bohdanhub.searchapp.domain.data.common.Result

data class ChildSearchResult(
    val parseResult: ParseResult,
    val childIds: List<Long>
)
