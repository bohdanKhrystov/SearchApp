package com.bohdanhub.searchapp.domain.data

sealed class Result<out T> {
    data class Success<T>(val result: T) : Result<T>()
    data class Failed(val throwable: Throwable) : Result<Nothing>()
}
