package com.bohdanhub.searchapp.data

import com.bohdanhub.searchapp.domain.Parser
import com.bohdanhub.searchapp.domain.UrlFetcher
import com.bohdanhub.searchapp.domain.data.*
import com.bohdanhub.searchapp.util.status
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.*
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRepository @Inject constructor(
    private val parser: Parser,
    private val fetcher: UrlFetcher
) {

    private val mutex = Mutex()
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val singleThreadDispatcher = Executors.newFixedThreadPool(1).asCoroutineDispatcher()

    private val _rootSearchResult: MutableStateFlow<RootSearchResult?> = MutableStateFlow(null)
    val rootSearchResult: StateFlow<RootSearchResult?> = _rootSearchResult

    private val searchJobs = mutableListOf<Job>()
    private val childSearchRequests: PriorityQueue<ChildSearchRequest> = PriorityQueue()
    private val childSearchResults: MutableStateFlow<List<ChildSearchResult>> =
        MutableStateFlow(listOf())

    private var rootSearchRequest: RootSearchRequest? = null

    init {
        scope.launch {
            while (true) {
                withContext(singleThreadDispatcher) {
                    searchJobs.removeAll { job ->
                        job.status() == "Cancelled" || job.status() == "Completed"
                    }
                    val canAddJob = searchJobs.size < MAX_SEARCH_JOBS_COUNT
                    if (canAddJob) {
                        val request = childSearchRequests.poll()
                        if (request != null) {
                            searchJobs.add(launch {
                                singleSearch(rootSearchRequest!!.textForSearch, request)
                            })
                        }
                    }
                }
                delay(100)
            }
        }
        childSearchResults
            .onEach {
                val currentResult = _rootSearchResult.value
                if (currentResult != null) {
                    var totalTextEntries = 0
                    val processedUrls = mutableListOf<String>()
                    val totalFoundedUrls = mutableListOf<String>()
                    println("////////////")
                    for (childResult in it) {
                        println(childResult)
                        processedUrls.add(childResult.request.url)
                        if (childResult.parseResult is Result.Success) {
                            totalTextEntries += childResult.parseResult.result.foundedTextEntries
                            totalFoundedUrls.addAll(childResult.parseResult.result.foundedUrls)
                        }
                    }
                    println("////////////")
                    val status =
                        if (processedUrls.containsAll(totalFoundedUrls))
                            RootSearchStatus.Completed
                        else
                            RootSearchStatus.InProgress
                    _rootSearchResult.value = currentResult.copy(
                        totalTextEntries = totalTextEntries,
                        foundedUrls = totalFoundedUrls,
                        processedUrls = processedUrls,
                        status = status
                    )
                }
            }.launchIn(scope)
    }

    suspend fun startSearch(request: RootSearchRequest) {
        this.rootSearchRequest = request
        childSearchResults.value = listOf()
        _rootSearchResult.value = RootSearchResult(
            request = request,
            totalTextEntries = 0,
            foundedUrls = listOf(),
            processedUrls = listOf(),
            status = RootSearchStatus.InProgress
        )
        mutex.withLock {
            childSearchRequests.add(
                ChildSearchRequest(
                    url = request.url,
                    parentId = 0,
                    id = 0,
                    deep = 0,
                )
            )
        }
    }

    private suspend fun singleSearch(textForSearch: String, request: ChildSearchRequest) {
        val childSearchResult = try {
            childSearch(textForSearch, request)
        } catch (e: Exception) {
            ChildSearchResult(
                request = request,
                parseResult = Result.Failed(e)
            )
        }
        mutex.withLock {
            val childSearchResultList = childSearchResults.value
            childSearchResults.value =
                childSearchResultList.toMutableList().apply { add(childSearchResult) }
        }
        val parseResult = childSearchResult.parseResult
        if (parseResult is Result.Success) {
            val foundedUrls = parseResult.result.foundedUrls
            val nextDeep = request.deep + 1
            for ((index, foundedUrl) in foundedUrls.withIndex()) {
                mutex.withLock {
                    childSearchRequests.add(
                        ChildSearchRequest(
                            url = foundedUrl,
                            id = index.toLong(),
                            deep = nextDeep,
                            parentId = request.id
                        )
                    )
                }
            }
        }
    }

    private suspend fun childSearch(
        textForSearch: String,
        request: ChildSearchRequest
    ): ChildSearchResult {
        return ChildSearchResult(
            request = request,
            parseResult = Result.Success(
                parser.parse(
                    originText = fetcher.fetch(request.url),
                    textForSearch = textForSearch
                )
            )
        )
    }

    companion object {
        private const val MAX_SEARCH_JOBS_COUNT: Int = 10
    }
}