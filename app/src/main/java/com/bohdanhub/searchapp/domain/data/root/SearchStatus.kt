package com.bohdanhub.searchapp.domain.data.root

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

    object InProgress : SearchStatus() {
        override fun toString(): String {
            return "InProgress"
        }
    }
}
