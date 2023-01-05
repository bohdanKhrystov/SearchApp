package com.bohdanhub.searchapp.domain.data.fetch

interface UrlFetcher {
    suspend fun fetch(url: String): String
}