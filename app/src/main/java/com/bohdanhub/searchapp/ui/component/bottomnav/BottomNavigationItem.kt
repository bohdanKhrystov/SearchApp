package com.bohdanhub.searchapp.ui.component.bottomnav

import com.bohdanhub.searchapp.R

sealed class BottomNavigationItem(var route: String, var icon: Int, var title: String) {
    object Search : BottomNavigationItem("search", R.drawable.ic_search, "Search")
    object History : BottomNavigationItem("history", R.drawable.ic_history, "History")
}
