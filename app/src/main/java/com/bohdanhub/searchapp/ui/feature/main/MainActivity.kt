package com.bohdanhub.searchapp.ui.feature.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bohdanhub.searchapp.ui.component.topappbar.TopAppBar
import com.bohdanhub.searchapp.ui.feature.details.DetailsScreen
import com.bohdanhub.searchapp.ui.feature.home.HomeScreen
import com.bohdanhub.searchapp.ui.feature.search.SearchScreen
import com.bohdanhub.searchapp.ui.feature.settings.SettingsScreen
import com.bohdanhub.searchapp.ui.theme.SearchAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SearchAppTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen(vm: MainViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    Scaffold(
        topBar = { TopAppBar(navController, vm) },
        content = { padding ->
            Box(modifier = Modifier.padding(padding)) {
                MainNavigation(navController = navController)
            }
        },
        backgroundColor = MaterialTheme.colors.background
    )
}

@Composable
fun MainNavigation(navController: NavHostController) {
    NavHost(navController, startDestination = MainScreens.Home.route) {
        composable(MainScreens.Home.route) {
            HomeScreen(navController)
        }
        composable(MainScreens.Settings.route) {
            SettingsScreen()
        }
        composable(MainScreens.SearchDetails.route) {
            DetailsScreen()
        }
    }
}

sealed class MainScreens(val route: String) {
    object Home : MainScreens("home")
    object Settings : MainScreens("settings")
    object SearchDetails : MainScreens("details")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SearchAppTheme {
        MainScreen()
    }
}