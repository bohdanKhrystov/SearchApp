package com.bohdanhub.searchapp.domain.data.root

import javax.annotation.concurrent.Immutable

@Immutable
data class SearchRequest(
    val textForSearch: String,
    val url: String,
)
