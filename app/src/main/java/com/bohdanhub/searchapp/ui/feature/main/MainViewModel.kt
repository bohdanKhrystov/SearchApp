package com.bohdanhub.searchapp.ui.feature.main

import android.util.Log
import androidx.lifecycle.ViewModel
import com.bohdanhub.searchapp.data.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
) : ViewModel() {

    fun test() {
        searchRepository.test()
        Log.d("MainViewModel", "test")
    }
}