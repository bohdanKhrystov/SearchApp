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

    private val notifyRequestsUpdated: MutableSharedFlow<ChildSearchRequest> = MutableSharedFlow()
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
                            mutex.withLock {
                                val inProgressRequest =
                                    request.copy(status = ChildRequestStatus.InProgress)
                                inProgressRequests.add(inProgressRequest)
                                notifyRequestsUpdated.emit(inProgressRequest)
                            }
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
            .onEach { childRequest ->
                _rootResult.update { currentResult ->
                    if (currentResult == null) return@update currentResult
                    var updatedStatus = currentResult.status
                    var updatedTextEntries = currentResult.textEntries
                    val updatedProcessedUrls = currentResult.processedUrls.toMutableList()
                    val updatedFoundedUrls = currentResult.foundedUrls.toMutableList()
                    val updatedChildRequests =
                        currentResult.childRequests?.toMutableMap() ?: mutableMapOf()

                    val childSearchRequests =
                        updatedChildRequests[childRequest.parentId] ?: listOf()
                    updatedChildRequests[childRequest.parentId] =
                        childSearchRequests.toMutableList().apply {
                            val i = indexOf(find { it.id == childRequest.id })
                            if (i == -1) {
                                add(childRequest)
                            } else {
                                this[i] = childRequest
                            }
                        }

                    val childRequestStatus = childRequest.status
                    if (childRequestStatus is ChildRequestStatus.Completed) {
                        updatedProcessedUrls.add(childRequest.url)
                        val childSearchResult = childRequestStatus.result
                        if (childSearchResult is Result.Success) {
                            updatedTextEntries += childSearchResult.data.parseResult.foundedTextEntries
                            updatedFoundedUrls.addAll(childSearchResult.data.parseResult.foundedUrls)
                        }
                        updatedStatus =
                            if (updatedProcessedUrls.containsAll(updatedFoundedUrls))
                                RootSearchStatus.Completed
                            else
                                RootSearchStatus.InProgress
                    }
                    currentResult.copy(
                        textEntries = updatedTextEntries,
                        foundedUrls = updatedFoundedUrls,
                        processedUrls = updatedProcessedUrls,
                        status = updatedStatus,
                        childRequests = updatedChildRequests,
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
            inProgressRequests.removeAll { it.id == completedRequest.id }
            completedRequests.add(completedRequest)
            notifyRequestsUpdated.emit(completedRequest)
        }
        val result = (completedRequest.status as? ChildRequestStatus.Completed)?.result
        if (result is Result.Success) {
            val foundedUrls = result.data.parseResult.foundedUrls
            val nextDeep = request.deep + 1
            for ((index, url) in foundedUrls.withIndex()) {
                mutex.withLock {
                    val id = result.data.childIds[index]
                    val queuedRequest = ChildSearchRequest(
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
                    queuedRequests.add(queuedRequest)
                    notifyRequestsUpdated.emit(queuedRequest)
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