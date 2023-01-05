package com.bohdanhub.searchapp.data

import com.bohdanhub.searchapp.domain.data.child.ChildRequestStatus
import com.bohdanhub.searchapp.domain.data.parse.Parser
import com.bohdanhub.searchapp.domain.data.fetch.UrlFetcher
import com.bohdanhub.searchapp.domain.data.child.ChildSearchRequest
import com.bohdanhub.searchapp.domain.data.child.ChildSearchResult
import com.bohdanhub.searchapp.domain.data.common.Result
import com.bohdanhub.searchapp.domain.data.root.RootSearchRequest
import com.bohdanhub.searchapp.domain.data.root.RootSearchResult
import com.bohdanhub.searchapp.domain.data.root.RootSearchStatus
import com.bohdanhub.searchapp.util.status
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
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
    private val _rootResult: MutableStateFlow<RootSearchResult?> = MutableStateFlow(null)
    val rootResult: StateFlow<RootSearchResult?> = _rootResult

    private val mutex = Mutex()
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val singleThreadDispatcher = Executors.newFixedThreadPool(1).asCoroutineDispatcher()
    private val searchJobs = mutableListOf<Job>()

    private val notifyRequestsUpdated: MutableSharedFlow<Unit> = MutableSharedFlow()
    private val queuedRequests: PriorityQueue<ChildSearchRequest> = PriorityQueue()
    private val completedRequests: MutableList<ChildSearchRequest> = mutableListOf()
    private val inProgressRequests: MutableList<ChildSearchRequest> = mutableListOf()

    private var rootSearchRequest: RootSearchRequest? = null
    private var requestId = 0L

    init {
        scope.launch {
            while (true) {
                withContext(singleThreadDispatcher) {
                    searchJobs.removeAll { job ->
                        job.status() == "Cancelled" || job.status() == "Completed"
                    }
                    val canAddJob = searchJobs.size < MAX_SEARCH_JOBS_COUNT
                    if (canAddJob) {
                        val request = mutex.withLock { queuedRequests.poll() }
                        if (request != null) {
                            searchJobs.add(scope.launch(Dispatchers.Default) {
                                singleSearch(rootSearchRequest!!.textForSearch, request)
                            })
                        }
                    }
                }
                delay(100)
            }
        }
        notifyRequestsUpdated
            .onEach {
                val currentResult = _rootResult.value
                if (currentResult != null) {
                    var totalTextEntries = 0
                    val processedUrls = mutableListOf<String>()
                    val totalFoundedUrls = mutableListOf<String>()
                    for (request in completedRequests) {
                        val result =
                            ((request.status as? ChildRequestStatus.Completed)?.result as? Result.Success)?.data
                        processedUrls.add(request.url)
                        totalTextEntries += result?.parseResult?.foundedTextEntries ?: 0
                        totalFoundedUrls.addAll(result?.parseResult?.foundedUrls ?: listOf())
                    }
                    val status =
                        if (processedUrls.containsAll(totalFoundedUrls))
                            RootSearchStatus.Completed
                        else
                            RootSearchStatus.InProgress
                    _rootResult.value = currentResult.copy(
                        textEntries = totalTextEntries,
                        foundedUrls = totalFoundedUrls,
                        processedUrls = processedUrls,
                        status = status
                    )
                }
            }.launchIn(scope)
    }

    suspend fun startSearch(request: RootSearchRequest) {
        this.rootSearchRequest = request
        requestId = 0
        completedRequests.clear()
        inProgressRequests.clear()
        queuedRequests.clear()
        _rootResult.value = RootSearchResult(
            request = request,
            textEntries = 0,
            foundedUrls = listOf(),
            processedUrls = listOf(),
            status = RootSearchStatus.InProgress
        )
        mutex.withLock {
            queuedRequests.add(
                ChildSearchRequest(
                    url = request.url,
                    parentId = -1,
                    id = requestId,
                    deep = 0,
                    priority = listOf(0),
                    status = ChildRequestStatus.Queued
                )
            )
        }
    }

    private suspend fun singleSearch(textForSearch: String, request: ChildSearchRequest) {
        val completedRequest = try {
            val searchResult = childSearch(textForSearch, request)
            request.copy(status = ChildRequestStatus.Completed(Result.Success(searchResult)))
        } catch (e: Exception) {
            request.copy(status = ChildRequestStatus.Completed(Result.Failed(e)))
        }
        mutex.withLock {
            completedRequests.add(completedRequest)
            notifyRequestsUpdated.emit(Unit)
        }
        val result = (completedRequest.status as? ChildRequestStatus.Completed)?.result
        if (result is Result.Success) {
            val foundedUrls = result.data.parseResult.foundedUrls
            val nextDeep = request.deep + 1
            for ((index, url) in foundedUrls.withIndex()) {
                mutex.withLock {
                    val id = result.data.childIds[index]
                    queuedRequests.add(
                        ChildSearchRequest(
                            url = url,
                            id = id,
                            deep = nextDeep,
                            parentId = request.id,
                            priority = ChildSearchRequest.calculatePriority(
                                id = id,
                                parentId = request.id,
                                completedRequests = completedRequests
                            ),
                            status = ChildRequestStatus.Queued,
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
        val parseResult = parser.parse(
            originText = fetcher.fetch(request.url),
            textForSearch = textForSearch
        )
        val childIds = mutex.withLock {
            parseResult.foundedUrls.map { generateId() }
        }
        return ChildSearchResult(
            parseResult = parseResult,
            childIds = childIds
        )
    }

    private fun generateId(): Long {
        requestId += 1
        return requestId
    }

    companion object {
        private const val MAX_SEARCH_JOBS_COUNT: Int = 7
    }
}