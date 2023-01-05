package com.bohdanhub.searchapp.domain.data.root

sealed class RootSearchStatus {
    object Paused : RootSearchStatus() {
        override fun toString(): String {
            return "Paused"
        }
    }

    object Completed : RootSearchStatus() {
        override fun toString(): String {
            return "Completed"
        }
    }

    object InProgress : RootSearchStatus() {
        override fun toString(): String {
            return "InProgress"
        }
    }
}
