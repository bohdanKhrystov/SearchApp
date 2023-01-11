package com.bohdanhub.searchapp.data

import com.bohdanhub.searchapp.domain.data.child.ChildRequestStatus
import com.bohdanhub.searchapp.domain.data.parse.Parser
import com.bohdanhub.searchapp.domain.data.fetch.UrlFetcher
import com.bohdanhub.searchapp.domain.data.child.ChildSearchRequest
import com.bohdanhub.searchapp.domain.data.child.ChildSearchResult
import com.bohdanhub.searchapp.domain.data.common.ConcurrentHashMap
import com.bohdanhub.searchapp.domain.data.common.ConcurrentPriorityQueue
import com.bohdanhub.searchapp.domain.data.common.Result
import com.bohdanhub.searchapp.domain.data.root.SearchRequest
import com.bohdanhub.searchapp.domain.data.root.SearchResult
import com.bohdanhub.searchapp.domain.data.root.SearchStatus
import com.bohdanhub.searchapp.util.status
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRepository @Inject constructor(
    private val parser: Parser,
    private val fetcher: UrlFetcher
) {
    private val _searchResult: MutableStateFlow<SearchResult?> = MutableStateFlow(null)
    val searchResult: StateFlow<SearchResult?> = _searchResult

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val singleThreadDispatcher = Executors.newFixedThreadPool(1).asCoroutineDispatcher()
    private val searchJobs = mutableListOf<Job>()

    private val requestsQueue = ConcurrentPriorityQueue<ChildSearchRequest>()
    private val requestsByParentId = ConcurrentHashMap<Long, List<ChildSearchRequest>>()
    private val requestFactory = ChildSearchRequest.Factory(requestsByParentId)

    private val notifyRequestsUpdated: MutableSharedFlow<ChildSearchRequest> = MutableSharedFlow()

    private var rootSearchRequest: SearchRequest? = null

    init {
        scope.launch {
            while (true) {
                withContext(singleThreadDispatcher) {
                    searchJobs.removeAll { job ->
                        job.status() == "Cancelled" || job.status() == "Completed"
                    }
                    val canAddJob = searchJobs.size < MAX_SEARCH_JOBS_COUNT
                    if (canAddJob) {
                        val request = requestsQueue.poll()
                        if (request != null) {
                            updateRequests(request.copy(status = ChildRequestStatus.InProgress))
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
                _searchResult.update { currentResult ->
                    if (currentResult == null) return@update currentResult
                    var updatedStatus = currentResult.status
                    var updatedTextEntries = currentResult.textEntries
                    val updatedProcessedUrls = currentResult.processedUrls.toMutableList()
                    val updatedFoundedUrls = currentResult.foundedUrls.toMutableList()

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
                                SearchStatus.Completed
                            else
                                SearchStatus.InProgress
                    }
                    currentResult.copy(
                        textEntries = updatedTextEntries,
                        foundedUrls = updatedFoundedUrls,
                        processedUrls = updatedProcessedUrls,
                        status = updatedStatus,
                        requestsByParentId = requestsByParentId.toMap(),
                    )
                }
            }.launchIn(scope)
    }

    suspend fun startSearch(request: SearchRequest) {
        this.rootSearchRequest = request
        ChildSearchResult.Factory.toDefault()
        requestsQueue.clear()
        requestsByParentId.clear()
        _searchResult.value = SearchResult(
            request = request,
            textEntries = 0,
            foundedUrls = listOf(),
            processedUrls = listOf(),
            status = SearchStatus.InProgress
        )
        requestsQueue.add(requestFactory.createRootRequest(request.url))
    }

    private suspend fun singleSearch(textForSearch: String, request: ChildSearchRequest) {
        val completedRequest = try {
            val searchResult = childSearch(textForSearch, request)
            request.copy(status = ChildRequestStatus.Completed(Result.Success(searchResult)))
        } catch (e: Exception) {
            request.copy(status = ChildRequestStatus.Completed(Result.Failed(e)))
        }
        updateRequests(completedRequest)
        for (childRequest in requestFactory.createRequests(completedRequest)) {
            requestsQueue.add(childRequest)
            updateRequests(childRequest)
        }
    }

    private suspend fun childSearch(
        textForSearch: String,
        request: ChildSearchRequest
    ): ChildSearchResult {
        return ChildSearchResult.Factory.createResult(
            parser.parse(
                originText = fetcher.fetch(request.url),
                textForSearch = textForSearch
            )
        )
    }

    private suspend fun updateRequests(request: ChildSearchRequest) {
        val childSearchRequests =
            requestsByParentId.get(request.parentId) ?: listOf()
        requestsByParentId.put(
            request.parentId,
            childSearchRequests.toMutableList().apply {
                val i = indexOf(find { it.id == request.id })
                if (i == -1) {
                    add(request)
                } else {
                    this[i] = request
                }
            }
        )
        notifyRequestsUpdated.emit(request)
    }

    companion object {
        private const val MAX_SEARCH_JOBS_COUNT: Int = 7
    }
}