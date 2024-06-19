package com.example.b09_wqga.navigation

sealed class Routes(val route: String) {
    object InitialScreen : Routes("InitialScreen")
    object LoginScreen : Routes("LoginScreen")
    object RegisterScreen : Routes("RegisterScreen")
    object WelcomeScreen : Routes("WelcomeScreen")
    object HomeScreen : Routes("HomeScreen/{userId}")
    object VocListScreen : Routes("VocListScreen/{userId}")
    object WordListScreen : Routes("WordListScreen/{vocId}")
    object GameListScreen : Routes("GameListScreen/{userId}")
    object GamePlayScreen_1 : Routes("GamePlayScreen_1/{vocId}")
    object GamePlayScreen_2 : Routes("GamePlayScreen_2/{vocId}")
    object ProfileScreen : Routes("ProfileScreen/{userId}")
    object MainScreen : Routes("MainScreen")
}
