package com.example.b09_wqga.navigation


sealed class Routes (val route: String) {
    object InitialScreen : Routes("InitialScreen")
    object LoginScreen : Routes("LoginScreen")
    object RegisterScreen : Routes("RegisterScreen")
    object WelcomeScreen : Routes("WelcomeScreen")
    object HomeScreen : Routes("HomeScreen")
    object VocListScreen : Routes("VocListScreen")
    object WordListScreen : Routes("WordListScreen")
    object GameListScreen : Routes("GameListScreen")
    object GamePlayScreen_1 : Routes("GamePlayScreen_1")
    object GamePlayScreen_2 : Routes("GamePlayScreen_2")
    object ProfileScreen : Routes("ProfileScreen")
    object MainScreen : Routes("MainScreen")

}