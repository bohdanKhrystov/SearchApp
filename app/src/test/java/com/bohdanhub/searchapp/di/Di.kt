package com.bohdanhub.searchapp.di

import com.bohdanhub.searchapp.domain.UrlFetcher

val testUrl = "https://to_replace.com"

fun provideMockFetcher(): UrlFetcher {
    return object : UrlFetcher {
        var maxIndex = 0
        override suspend fun fetch(url: String): String {
            val result = StringBuilder()
            result.append("{\n")
            if (maxIndex < 10) {
                for (i in maxIndex + 1..maxIndex + 3) {
                    if (i > maxIndex) maxIndex = i
                    result.append(testUrl.replace("to_replace", "$i"))
                    result.append(",\n")
                }
            }
            result.append("\n}")
            return result.toString()
        }
    }
}

fun provideMockFetcherThatThrowErrorSomeTimes(): UrlFetcher {
    return object : UrlFetcher {
        var maxIndex = 0
        var throwIndex = 11
        override suspend fun fetch(url: String): String {
            if (throwIndex % 5 == 0) {
                throwIndex++
                throw RuntimeException("Mock network error")
            }
            throwIndex++
            val result = StringBuilder()
            result.append("{\n")
            if (maxIndex < 10) {
                for (i in maxIndex + 1..maxIndex + 3) {
                    if (i > maxIndex) maxIndex = i
                    result.append(testUrl.replace("to_replace", "$i"))
                    result.append(",\n")
                }
            }
            result.append("\n}")
            return result.toString()
        }
    }
}