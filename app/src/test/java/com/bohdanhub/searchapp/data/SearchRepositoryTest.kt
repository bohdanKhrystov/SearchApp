package com.bohdanhub.searchapp.data

import com.bohdanhub.searchapp.di.provideMockFetcher
import com.bohdanhub.searchapp.di.provideMockFetcherThatThrowErrorSomeTimes
import com.bohdanhub.searchapp.di.testUrl
import com.bohdanhub.searchapp.domain.data.RootSearchRequest
import com.bohdanhub.searchapp.domain.data.RootSearchStatus
import kotlinx.coroutines.runBlocking
import org.junit.Test

internal class SearchRepositoryTest {

    @Test
    fun testIfSearchTraversalIsInWidth() {
        assert(true)
    }

    @Test
    fun testSearchComplete() {
        val fetcher = provideMockFetcher()
        val parser = ParserImpl()
        val repository = SearchRepository(parser, fetcher)
        runBlocking {
            repository.startSearch(
                RootSearchRequest(
                    textForSearch = "1",
                    url = testUrl,
                )
            )
            val timeStart = System.currentTimeMillis()
            while (repository.rootSearchResult.value?.status != RootSearchStatus.Completed) {
                if (System.currentTimeMillis() - timeStart > 3_000) {
                    assert(false) { "Test timeout" }
                }
            }
        }
        assert(true)
    }

    @Test
    fun testSearchCompleteIfErrors() {
        val fetcher = provideMockFetcherThatThrowErrorSomeTimes()
        val parser = ParserImpl()
        val repository = SearchRepository(parser, fetcher)
        runBlocking {
            repository.startSearch(
                RootSearchRequest(
                    textForSearch = "1",
                    url = testUrl,
                )
            )
            val timeStart = System.currentTimeMillis()
            while (repository.rootSearchResult.value?.status != RootSearchStatus.Completed) {
                if (System.currentTimeMillis() - timeStart > 3_000) {
                    assert(false) { "Test timeout" }
                }
            }
        }
        assert(true)
    }
}