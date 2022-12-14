package com.bohdanhub.searchapp.ui.component.topappbar

import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.bohdanhub.searchapp.ui.feature.main.MainScreens
import com.bohdanhub.searchapp.ui.feature.main.MainViewModel

@Composable
fun TopAppBar(navController: NavHostController, vm: MainViewModel) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    Column {
        TopAppBar(
            elevation = 4.dp,
            title = {
                Text("SearchApp")
            },
            backgroundColor = MaterialTheme.colors.primarySurface,
            navigationIcon = {
                if (currentRoute != MainScreens.Home.route) {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Filled.ArrowBack, null)
                    }
                }
            }, actions = {
                IconButton(onClick = {
                    vm.search()
                    navController.navigate(MainScreens.Settings.route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }) {
                    Icon(Icons.Filled.Settings, null)
                }
            })
    }
}