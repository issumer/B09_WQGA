package com.example.b09_wqga.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.b09_wqga.screen.*
import com.example.b09_wqga.repository.UserRepository
import com.example.b09_wqga.viewmodelfactory.UserViewModelFactory
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.b09_wqga.viewmodel.UserViewModel

fun NavGraphBuilder.MainNavGraph(navController: NavHostController) {
    navigation(startDestination = Routes.HomeScreen.route, route = "MainScreen") {

        composable(route = Routes.HomeScreen.route) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
            val userRepository = UserRepository()
            val userViewModel: UserViewModel = viewModel(factory = UserViewModelFactory(userRepository))
            HomeScreen(userId, userViewModel)
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
