package com.bohdanhub.searchapp.ui.feature.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bohdanhub.searchapp.data.SearchRepository
import com.bohdanhub.searchapp.domain.data.RootSearchRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
) : ViewModel() {

    fun search() {
        viewModelScope.launch {
            val r = searchRepository.startSearch(
                RootSearchRequest(
                    textForSearch = "a",
                    url = "https://github.com/meltaran777/TestSearch/blob/master/app/src/main/java/com/bohdan/khristov/textsearch/util/StringExt.kt"
                )
            )
            Log.d("SearchTest","r = $r")
        }
    }
}