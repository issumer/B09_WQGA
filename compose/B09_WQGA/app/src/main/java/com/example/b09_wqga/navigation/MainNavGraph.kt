package com.example.b09_wqga.navigation

import androidx.compose.ui.input.key.Key.Companion.Home
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.b09_wqga.screen.GameListScreen
import com.example.b09_wqga.screen.GamePlayScreen_1
import com.example.b09_wqga.screen.GamePlayScreen_2
import com.example.b09_wqga.screen.ProfileScreen
import com.example.b09_wqga.screen.HomeScreen
import com.example.b09_wqga.screen.VocListScreen
import com.example.b09_wqga.screen.WordListScreen


fun NavGraphBuilder.MainNavGraph(navController: NavHostController){
    navigation(startDestination = "HomeScreen", route="MainScreen"){

        composable(route = Routes.HomeScreen.route) {
            HomeScreen()
        }
        composable(route = Routes.VocListScreen.route) {
            VocListScreen(navController)
        }
        composable(route = Routes.WordListScreen.route) {
            WordListScreen()
        }
        composable(route = Routes.GameListScreen.route) {
            GameListScreen(navController)
        }
        composable(route = Routes.GamePlayScreen_1.route) {
            GamePlayScreen_1(navController)
        }
        composable(route = Routes.GamePlayScreen_2.route) {
            GamePlayScreen_2(navController)
        }
        composable(route = Routes.ProfileScreen.route) {
            ProfileScreen()
        }
    }
}