package com.bohdanhub.searchapp.ui.feature.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bohdanhub.searchapp.data.SearchRepository
import com.bohdanhub.searchapp.domain.data.SearchRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
) : ViewModel() {

    fun search(request: SearchRequest) {
        viewModelScope.launch {
            searchRepository.startSearch(request).also {
                Log.d("SearchTest", "result = $it")
            }
        }
    }
}