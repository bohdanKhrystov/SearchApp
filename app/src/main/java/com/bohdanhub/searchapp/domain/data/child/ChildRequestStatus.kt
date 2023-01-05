package com.bohdanhub.searchapp.domain.data.child

import com.bohdanhub.searchapp.domain.data.common.Result

sealed class ChildRequestStatus {
    object New : ChildRequestStatus()
    object Queued : ChildRequestStatus()
    object InProgress : ChildRequestStatus()
    class Completed(val result: Result<ChildSearchResult>) : ChildRequestStatus()
}
