package com.bohdanhub.searchapp.domain.data.common

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Failed(val throwable: Throwable) : Result<Nothing>()
}
