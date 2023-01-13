package com.bohdanhub.searchapp.ui.feature.details

import androidx.lifecycle.ViewModel
import com.bohdanhub.searchapp.data.SearchRepository
import com.bohdanhub.searchapp.domain.data.root.SearchResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
) : ViewModel() {

    private val notNullResults = searchRepository.searchResult.filterNotNull()
    val searchResult: Flow<SearchResult> = merge(
        notNullResults.take(1),
        notNullResults.drop(1).debounce(3_000),
    )
}