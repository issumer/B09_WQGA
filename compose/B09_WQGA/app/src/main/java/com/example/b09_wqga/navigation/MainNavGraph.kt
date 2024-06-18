package com.example.b09_wqga.navigation

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.b09_wqga.repository.UserRepository
import com.example.b09_wqga.screen.*
import com.example.b09_wqga.viewmodelfactory.UserViewModelFactory
import com.example.b09_wqga.viewmodel.UserViewModel
import com.example.b09_wqga.model.UserDataViewModel
import com.example.b09_wqga.repository.VocRepository
import com.example.b09_wqga.viewmodel.VocViewModel
import com.example.b09_wqga.viewmodelfactory.VocViewModelFactory

fun NavGraphBuilder.MainNavGraph(navController: NavHostController) {
    navigation(startDestination = Routes.HomeScreen.route, route = "MainScreen") {

        composable(route = Routes.HomeScreen.route) { backStackEntry ->
            val userRepository = UserRepository()
            val userViewModel: UserViewModel = viewModel(factory = UserViewModelFactory(userRepository))
            val userDataViewModel: UserDataViewModel = viewModel(viewModelStoreOwner = LocalNavGraphViewModelStoreOwner.current)
            val userId = userDataViewModel.userID.value.toIntOrNull() ?: return@composable
            HomeScreen(userId.toString(), userViewModel)
        }
        composable(route = Routes.VocListScreen.route) { backStackEntry ->
            val userDataViewModel: UserDataViewModel = viewModel(viewModelStoreOwner = LocalNavGraphViewModelStoreOwner.current)
            val userId = userDataViewModel.userID.value.toIntOrNull() ?: return@composable
            VocListScreen(navController, userId)
        }
        composable(route = Routes.WordListScreen.route) { backStackEntry ->
            val vocId = backStackEntry.arguments?.getString("vocId")?.toIntOrNull() ?: return@composable
            WordListScreen(navController, vocId)
        }
        composable(route = Routes.GameListScreen.route) { backStackEntry ->
            val userDataViewModel: UserDataViewModel = viewModel(viewModelStoreOwner = LocalNavGraphViewModelStoreOwner.current)
            val userId = userDataViewModel.userID.value.toIntOrNull() ?: return@composable
            GameListScreen(navController, userId)
        }
        composable(route = Routes.GamePlayScreen_1.route) { backStackEntry ->
            val vocId = backStackEntry.arguments?.getString("vocId")?.toIntOrNull() ?: return@composable
            val vocRepository = VocRepository()
            val vocViewModel: VocViewModel = viewModel(factory = VocViewModelFactory(vocRepository))
            GamePlayScreen_1(navController, vocId!!, vocViewModel)
        }
        composable(route = Routes.GamePlayScreen_2.route) {
            GamePlayScreen_2(navController)
        }
        composable(route = Routes.ProfileScreen.route) { backStackEntry ->
            val userRepository = UserRepository()
            val userViewModel: UserViewModel = viewModel(factory = UserViewModelFactory(userRepository))
            val userDataViewModel: UserDataViewModel = viewModel(viewModelStoreOwner = LocalNavGraphViewModelStoreOwner.current)
            val userId = userDataViewModel.userID.value.toIntOrNull() ?: return@composable
            ProfileScreen(navController, userId)
        }
    }
}
