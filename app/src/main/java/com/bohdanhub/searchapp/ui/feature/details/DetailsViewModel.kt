package com.bohdanhub.searchapp.ui.feature.details

import androidx.lifecycle.ViewModel
import com.bohdanhub.searchapp.data.SearchRepository
import com.bohdanhub.searchapp.domain.data.root.SearchResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
) : ViewModel() {

    val searchResult: Flow<SearchResult> = searchRepository.searchResult.filterNotNull()
}