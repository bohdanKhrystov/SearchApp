package com.bohdanhub.searchapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.bohdanhub.searchapp.ui.component.bottomnav.BottomNavigationBar
import com.bohdanhub.searchapp.ui.feature.home.HomeScreen
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
    val navController: NavHostController = rememberNavController()
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) },
        content = { padding: PaddingValues -> // We have to pass the scaffold inner padding to our content. That's why we use Box.
            HomeScreen(padding = padding, navController = navController)
        },
        backgroundColor = MaterialTheme.colors.background // Set background color to avoid the white flashing when you switch between screens
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SearchAppTheme {
        MainScreen()
    }
}