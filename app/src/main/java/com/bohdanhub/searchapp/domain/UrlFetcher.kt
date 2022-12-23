package com.bohdanhub.searchapp.domain

interface UrlFetcher {
    suspend fun fetch(url: String): String
}