package com.example.b09_wqga.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.b09_wqga.screen.*
import com.example.b09_wqga.repository.UserRepository
import com.example.b09_wqga.viewmodelfactory.UserViewModelFactory
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.b09_wqga.model.UserDataViewModel
import com.example.b09_wqga.viewmodel.UserViewModel

fun NavGraphBuilder.MainNavGraph(navController: NavHostController) {
    navigation(startDestination = Routes.HomeScreen.route, route = "MainScreen") {

        composable(route = Routes.HomeScreen.route) { backStackEntry ->
            //val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
            // 임시 패치
            val userRepository = UserRepository()
            val userViewModel: UserViewModel = viewModel(factory = UserViewModelFactory(userRepository))
            val userDataViewModel: UserDataViewModel = viewModel(viewModelStoreOwner = LocalNavGraphViewModelStoreOwner.current)
            val userId = userDataViewModel.userID.value
            HomeScreen(userId, userViewModel)
        }
        composable(route = Routes.VocListScreen.route) { backStackEntry ->
            //val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
            // 임시 패치
            val userDataViewModel: UserDataViewModel = viewModel(viewModelStoreOwner = LocalNavGraphViewModelStoreOwner.current)
            val userId = userDataViewModel.userID.value
            VocListScreen(navController, userId.toInt())
        }
        composable(route = Routes.WordListScreen.route) { backStackEntry ->
            val vocId = backStackEntry.arguments?.getString("vocId")?.toIntOrNull() ?: return@composable
            WordListScreen(navController, vocId)
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
        composable(route = Routes.ProfileScreen.route) { backStackEntry ->
            //val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
            val userRepository = UserRepository()
            val userViewModel: UserViewModel = viewModel(factory = UserViewModelFactory(userRepository))
            // 임시 패치
            val userDataViewModel: UserDataViewModel = viewModel(viewModelStoreOwner = LocalNavGraphViewModelStoreOwner.current)
            val userId = userDataViewModel.userID.value
            ProfileScreen(navController, userId)
        }
    }
}
