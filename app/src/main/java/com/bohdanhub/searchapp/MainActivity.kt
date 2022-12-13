package com.bohdanhub.searchapp

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
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bohdanhub.searchapp.ui.component.topappbar.TopAppBar
import com.bohdanhub.searchapp.ui.feature.home.HomeScreen
import com.bohdanhub.searchapp.ui.feature.settings.SettingsScreen
import com.bohdanhub.searchapp.ui.theme.SearchAppTheme

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
fun MainScreen() {
    val navController = rememberNavController()
    Scaffold(
        topBar = { TopAppBar(navController) },
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
            HomeScreen()
        }
        composable(MainScreens.Settings.route) {
            SettingsScreen()
        }
    }
}

sealed class MainScreens(val route: String) {
    object Home : MainScreens("home")
    object Settings : MainScreens("settings")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SearchAppTheme {
        MainScreen()
    }
}