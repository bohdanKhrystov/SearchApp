package com.bohdanhub.searchapp.ui.feature.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bohdanhub.searchapp.data.SearchRepository
import com.bohdanhub.searchapp.domain.data.RootSearchRequest
import com.bohdanhub.searchapp.domain.data.RootSearchResult
import com.bohdanhub.searchapp.ui.component.card.SearchCard
import com.bohdanhub.searchapp.ui.component.card.SearchCardData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
) : ViewModel() {

    val searchCardData: Flow<SearchCardData?> = searchRepository.rootSearchResult
        .filterNotNull()
        .map { searchResult ->
            SearchCardData(
                request = searchResult.request,
                totalUrlsCount = searchResult.foundedUrls.size,
                processedUrlsCount = searchResult.processedUrls.size,
                latestUrl = searchResult.processedUrls.lastOrNull() ?: "",
                progress = searchResult.getProgress(),
                status = searchResult.status,
                textEntriesCount = searchResult.totalTextEntries
            )
        }

    fun search(request: RootSearchRequest) {
        viewModelScope.launch {
            searchRepository.startSearch(request)
        }
    }
}