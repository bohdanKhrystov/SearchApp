package com.bohdanhub.searchapp.data

import com.bohdanhub.searchapp.di.provideMockFetcher
import com.bohdanhub.searchapp.di.provideMockFetcherThatFetchWithDelays
import com.bohdanhub.searchapp.di.provideMockFetcherThatThrowErrorSomeTimes
import com.bohdanhub.searchapp.di.rootTestUrl
import com.bohdanhub.searchapp.domain.data.root.RootSearchRequest
import com.bohdanhub.searchapp.domain.data.root.RootSearchStatus
import com.bohdanhub.searchapp.util.deepEqualTo
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.net.URL

internal class SearchRepositoryTest {

    @Test
    fun testSearchTraversalIsInWidth() {
        val fetcher = provideMockFetcher()
        val parser = ParserImpl()
        val repository = SearchRepository(parser, fetcher)
        runBlocking {
            repository.startSearch(
                RootSearchRequest(
                    textForSearch = "1",
                    url = rootTestUrl,
                )
            )
            val timeStart = System.currentTimeMillis()
            while (repository.rootSearchResult.value?.status != RootSearchStatus.Completed) {
                if (System.currentTimeMillis() - timeStart > 100_000) {
                    assert(false) { "Test timeout" }
                }
            }
            val foundedUrls = repository.rootSearchResult.value!!.foundedUrls
            assert(foundedUrls.deepEqualTo(expectedFoundedUrls)) {
                "Search is not in width, founded urls:\n$foundedUrls\n do not match expected:\n$expectedFoundedUrls\n"
            }
        }
    }


    @Test
    fun testSearchTraversalIsInWidthIfDelays() {
        val fetcher = provideMockFetcherThatFetchWithDelays()
        val parser = ParserImpl()
        val repository = SearchRepository(parser, fetcher)
        runBlocking {
            repository.startSearch(
                RootSearchRequest(
                    textForSearch = "1",
                    url = rootTestUrl,
                )
            )
            val timeStart = System.currentTimeMillis()
            while (repository.rootSearchResult.value?.status != RootSearchStatus.Completed) {
                if (System.currentTimeMillis() - timeStart > 100_000) {
                    assert(false) { "Test timeout" }
                }
            }
            val foundedUrls = repository.rootSearchResult.value!!.foundedUrls
            val indexes = foundedUrls.map {
                URL(it).host.split(".").first().toInt()
            }
            var i67 = -1
            var i68 = -1
            var i69 = -1
            var i70 = -1
            var i71 = -1
            var i72 = -1
            var i73 = -1
            for ((index, value) in indexes.withIndex()) {
                if (value == 67) i67 = index
                if (value == 68) i68 = index
                if (value == 69) i69 = index
                if (value == 70) i70 = index
                if (value == 71) i71 = index
                if (value == 72) i72 = index
                if (value == 73) i73 = index
            }
            assert(i67 < i68 && i67 < i69 && i67 < i70 && i67 < i71 && i67 < i72 && i67 < i73) {
                "Search is not in width:\ni67 = $i67\n" +
                        "i68 = $i68\n" +
                        "i69 = $i69\n" +
                        "i70 = $i70\n" +
                        "i71 = $i71\n" +
                        "i72 = $i72\n" +
                        "i73 = $i73\n"
            }
        }
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
                    url = rootTestUrl,
                )
            )
            val timeStart = System.currentTimeMillis()
            while (repository.rootSearchResult.value?.status != RootSearchStatus.Completed) {
                if (System.currentTimeMillis() - timeStart > 100_000) {
                    assert(false) { "Test timeout" }
                }
            }
        }
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
                    url = rootTestUrl,
                )
            )
            val timeStart = System.currentTimeMillis()
            while (repository.rootSearchResult.value?.status != RootSearchStatus.Completed) {
                if (System.currentTimeMillis() - timeStart > 100_000) {
                    assert(false) { "Test timeout" }
                }
            }
        }
    }
}

val expectedFoundedUrls =
    listOf(
        "https://1.com",
        "https://2.com",
        "https://3.com",
        "https://4.com",
        "https://5.com",
        "https://6.com",
        "https://7.com",
        "https://8.com",
        "https://9.com",
        "https://10.com",
        "https://11.com",
        "https://12.com",
        "https://13.com",
        "https://14.com",
        "https://15.com",
        "https://16.com",
        "https://17.com",
        "https://18.com",
        "https://19.com",
        "https://20.com",
        "https://21.com",
        "https://22.com",
        "https://23.com",
        "https://24.com",
        "https://25.com",
        "https://26.com",
        "https://27.com",
        "https://28.com",
        "https://29.com",
        "https://30.com",
        "https://31.com",
        "https://32.com",
        "https://33.com",
        "https://34.com",
        "https://35.com",
        "https://36.com",
        "https://37.com",
        "https://38.com",
        "https://39.com",
        "https://40.com",
        "https://41.com",
        "https://42.com",
        "https://43.com",
        "https://44.com",
        "https://45.com",
        "https://46.com",
        "https://47.com",
        "https://48.com",
        "https://49.com",
        "https://50.com",
        "https://51.com",
        "https://52.com",
        "https://53.com",
        "https://54.com",
        "https://55.com",
        "https://56.com",
        "https://57.com",
        "https://58.com",
        "https://59.com",
        "https://60.com",
        "https://61.com",
        "https://62.com",
        "https://63.com",
        "https://64.com",
        "https://65.com",
        "https://66.com",
        "https://67.com",
        "https://68.com",
        "https://69.com",
        "https://70.com",
        "https://71.com",
        "https://72.com",
        "https://73.com",
        "https://74.com",
        "https://75.com",
    )