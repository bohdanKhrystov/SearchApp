package com.bohdanhub.searchapp.ui.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bohdanhub.searchapp.data.SearchRepository
import com.bohdanhub.searchapp.domain.data.root.RootSearchRequest
import com.bohdanhub.searchapp.ui.component.card.SearchCardData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
) : ViewModel() {

    val searchCardData: Flow<SearchCardData?> = searchRepository.rootResult
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

    fun search(request: RootSearchRequest) {
        viewModelScope.launch {
            searchRepository.startSearch(request)
        }
    }
}