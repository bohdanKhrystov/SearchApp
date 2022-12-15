package com.bohdanhub.searchapp.domain.data

sealed class SearchStatus {
    object Paused : SearchStatus() {
        override fun toString(): String {
            return "Paused"
        }
    }

    object Completed : SearchStatus() {
        override fun toString(): String {
            return "Completed"
        }
    }

    class InProgress(val progress: Float) : SearchStatus() {
        override fun toString(): String {
            return "InProgress $progress"
        }
    }
}
