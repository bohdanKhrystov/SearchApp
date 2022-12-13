package com.bohdanhub.searchapp.ui.feature.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.bohdanhub.searchapp.ui.component.bottomnav.BottomNavigationItem
import com.bohdanhub.searchapp.ui.feature.history.HistoryScreen
import com.bohdanhub.searchapp.ui.feature.search.SearchScreen

@Composable
fun HomeScreen(padding: PaddingValues, navController: NavHostController) {
    Box(modifier = Modifier.padding(padding)) {
        HomeNavigation(navController = navController)
    }
}

@Composable
fun HomeNavigation(navController: NavHostController) {
    NavHost(navController, startDestination = BottomNavigationItem.Search.route) {
        composable(BottomNavigationItem.Search.route) {
            SearchScreen()
        }
        composable(BottomNavigationItem.History.route) {
            HistoryScreen()
        }
    }
}