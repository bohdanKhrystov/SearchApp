package com.bohdanhub.searchapp.util

import kotlinx.coroutines.Job

fun Job.status(): String = when {
    isActive -> "Active/Completing"
    isCompleted && isCancelled -> "Cancelled"
    isCancelled -> "Cancelling"
    isCompleted -> "Completed"
    else -> "New"
}