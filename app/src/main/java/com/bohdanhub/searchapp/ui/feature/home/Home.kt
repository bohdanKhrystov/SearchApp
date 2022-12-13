package com.bohdanhub.searchapp.ui.feature.home

import androidx.compose.foundation.layout.Box
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bohdanhub.searchapp.ui.component.bottomnav.BottomNavigationBar
import com.bohdanhub.searchapp.ui.component.bottomnav.BottomNavigationItem
import com.bohdanhub.searchapp.ui.feature.history.HistoryScreen
import com.bohdanhub.searchapp.ui.feature.search.SearchScreen

@Composable
fun HomeScreen() {
    val navController: NavHostController = rememberNavController()
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) },
        content = {
            Box {
                HomeNavigation(navController = navController)
            }
        },
        backgroundColor = MaterialTheme.colors.background
    )
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