package com.bohdanhub.searchapp.ui.feature.search

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bohdanhub.searchapp.data.SearchRepository
import com.bohdanhub.searchapp.domain.data.root.SearchRequest
import com.bohdanhub.searchapp.domain.data.root.SearchStatus
import com.bohdanhub.searchapp.ui.component.card.SearchCardData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
) : ViewModel() {

    val isSearchInProgress = mutableStateOf<Boolean>(false)
    val requestState = mutableStateOf<SearchRequest?>(null)
    val textEntriesState = mutableStateOf<Int>(0)
    val latestUrlState = mutableStateOf<String>("")
    val progressState = mutableStateOf<Float>(0.0f)
    val processedUrlsCountState = mutableStateOf<Int>(0)
    val totalUrlsCountState = mutableStateOf<Int>(0)
    val statusState = mutableStateOf<SearchStatus?>(null)

    init {
        searchRepository.searchResult
            .filterNotNull()
            .map { searchResult ->
                SearchCardData(
                    request = searchResult.request,
                    totalUrlsCount = searchResult.foundedUrls.size,
                    processedUrlsCount = searchResult.processedUrls.size,
                    latestUrl = searchResult.processedUrls.lastOrNull() ?: "",
                    progress = searchResult.getProgress(),
                    status = searchResult.status,
                    textEntriesCount = searchResult.textEntries
                )
            }
            .onEach {
                isSearchInProgress.value = it.status is SearchStatus.InProgress
                textEntriesState.value = it.textEntriesCount
                latestUrlState.value = it.latestUrl
                progressState.value = it.progress
                processedUrlsCountState.value = it.processedUrlsCount
                totalUrlsCountState.value = it.totalUrlsCount
                statusState.value = it.status
                requestState.value = it.request
            }
            .launchIn(viewModelScope)
    }

    fun search(request: SearchRequest) {
        viewModelScope.launch {
            searchRepository.startSearch(request)
        }
    }
}