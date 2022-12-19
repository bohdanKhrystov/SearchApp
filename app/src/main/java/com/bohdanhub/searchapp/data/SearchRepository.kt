package com.bohdanhub.searchapp.data

import com.bohdanhub.searchapp.domain.data.*
import com.bohdanhub.searchapp.util.countEntries
import com.bohdanhub.searchapp.util.extractUrls
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.*
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRepository @Inject constructor() {

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
                    for (childRequest in it) {
                        totalTextEntries += childRequest.parseResult.foundedTextEntries
                        processedUrls.add(childRequest.request.url)
                        totalFoundedUrls.addAll(childRequest.parseResult.foundedUrls)
                    }
                    _rootSearchResult.value = currentResult.copy(
                        totalTextEntries = totalTextEntries,
                        foundedUrls = totalFoundedUrls,
                        processedUrls = processedUrls,
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
            status = SearchStatus.InProgress
        )
        mutex.withLock {
            childSearchRequests.add(ChildSearchRequest(request.url, 0, 0, 0))
        }
    }

    private suspend fun singleSearch(textForSearch: String, request: ChildSearchRequest) {
        val childSearchResult = childSearch(textForSearch, request)
        mutex.withLock {
            val childSearchResultList = childSearchResults.value
            childSearchResults.value =
                childSearchResultList.toMutableList().apply { add(childSearchResult) }
        }
        val foundedUrls = childSearchResult.parseResult.foundedUrls
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

    private suspend fun childSearch(
        textForSearch: String,
        request: ChildSearchRequest
    ): ChildSearchResult {
        return ChildSearchResult(
            request = request,
            parseResult = parseText(
                originText = fetchUrl(request.url),
                textForSearch = textForSearch
            )
        )
    }

    private suspend fun parseText(textForSearch: String, originText: String): ParseResult =
        withContext(Dispatchers.Default) {
            ParseResult(
                foundedTextEntries = originText.countEntries(textForSearch),
                foundedUrls = originText.extractUrls()
            )
        }

    private suspend fun fetchUrl(url: String): String = withContext(Dispatchers.IO) {
        var urlConnection: HttpURLConnection? = null
        var result = ""
        try {
            val urlObj = URL(url)
            urlConnection = urlObj.openConnection() as HttpURLConnection
            val code = urlConnection.responseCode
            //Log.d("SearchRepository", "Code = $code")
            if (code == 200) {
                val stream = BufferedInputStream(urlConnection.inputStream)
                val bufferedReader = BufferedReader(InputStreamReader(stream))
                var line: String?
                while (run {
                        line = bufferedReader.readLine()
                        line
                    } != null) {
                    //Log.d("SearchRepository", "line = $line")
                    result += line
                }
                stream.close()
            }
            return@withContext result
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            urlConnection?.disconnect()
        }
        return@withContext result
    }

    fun Job.status(): String = when {
        isActive -> "Active/Completing"
        isCompleted && isCancelled -> "Cancelled"
        isCancelled -> "Cancelling"
        isCompleted -> "Completed"
        else -> "New"
    }

    companion object {
        private const val MAX_SEARCH_JOBS_COUNT: Int = 10
    }
}