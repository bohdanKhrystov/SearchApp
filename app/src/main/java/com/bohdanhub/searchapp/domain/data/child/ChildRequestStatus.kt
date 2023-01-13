package com.bohdanhub.searchapp.domain.data.child

import com.bohdanhub.searchapp.domain.data.common.Result

sealed class ChildRequestStatus {
    object New : ChildRequestStatus() {
        override fun toString(): String {
            return "New"
        }
    }

    object Queued : ChildRequestStatus() {
        override fun toString(): String {
            return "Queued"
        }
    }

    object InProgress : ChildRequestStatus() {
        override fun toString(): String {
            return "InProgress"
        }
    }

    class Completed(val result: Result<ChildSearchResult>) : ChildRequestStatus() {
        override fun toString(): String {
            return "Competed"
        }
    }
}
