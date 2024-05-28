package com.example.b09_wqga.screen_test

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.b09_wqga.component.BottomNavigationBar
import com.example.b09_wqga.ui.theme.B09_WQGATheme

/*
@Composable
fun MyAppTest() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "initial") {
        composable("initial") { InitialScreenNavTest(navController) }
        composable("login") { LoginScreenNavTest(navController) }
        composable("register") { RegisterScreenNavTest(navController) }
        composable("main") { MainScreenNavTest(navController) }
        composable("home") { HomeScreenNavTest() }
        composable("voclist") { VocListScreenNavTest(navController) }
        composable("gamelist") { GameListScreenNavTest(navController) }
        composable("profile") { ProfileScreenNavTest(navController) }
        composable("wordlist") { WordListScreenNavTest(navController) }
        composable("gameplay") { GamePlayScreenNavTest(navController) }
    }
}

@Composable
fun InitialScreenNavTest(navController: NavHostController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { navController.navigate("login") }) {
            Text("Login")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.navigate("register") }) {
            Text("Register")
        }
    }
}

@Composable
fun LoginScreenNavTest(navController: NavHostController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Login Screen")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.navigate("main") }) {
            Text("Login")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.navigate("register") }) {
            Text("Go to Register")
        }
    }
}

@Composable
fun RegisterScreenNavTest(navController: NavHostController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Register Screen")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.navigate("main") }) {
            Text("Register")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.navigate("login") }) {
            Text("Go to Login")
        }
    }
}

@Composable
fun MainScreenNavTest(navController: NavHostController) {
    val mainNavController = rememberNavController()
    Scaffold(
        bottomBar = {
            BottomNavigationBar(mainNavController)
        }
    ) { innerPadding ->
        NavHost(
            navController = mainNavController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") { HomeScreenNavTest() }
            composable("voclist") { VocListScreenNavTest(navController) }
            composable("gamelist") { GameListScreenNavTest(navController) }
            composable("profile") { ProfileScreenNavTest(navController) }
        }
    }
}

@Composable
fun BottomNavigationBarNavTest(navController: NavHostController) {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            label = { Text("Home") },
            selected = navController.currentDestination?.route == "home",
            onClick = { navController.navigate("home") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null) },
            label = { Text("Vocabulary") },
            selected = navController.currentDestination?.route == "voclist",
            onClick = { navController.navigate("voclist") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Gamepad, contentDescription = null) },
            label = { Text("Games") },
            selected = navController.currentDestination?.route == "gamelist",
            onClick = { navController.navigate("gamelist") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = null) },
            label = { Text("Profile") },
            selected = navController.currentDestination?.route == "profile",
            onClick = { navController.navigate("profile") }
        )
    }
}

@Composable
fun HomeScreenNavTest() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Home Screen")
    }
}

@Composable
fun VocListScreenNavTest(navController: NavHostController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Vocabulary List Screen")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.navigate("wordlist") }) {
            Text("Go to Word List")
        }
    }
}

@Composable
fun GameListScreenNavTest(navController: NavHostController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Game List Screen")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.navigate("gameplay") }) {
            Text("Go to Game Play")
        }
    }
}

@Composable
fun ProfileScreenNavTest(navController: NavHostController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Profile Screen")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.navigate("initial") }) {
            Text("Log Out")
        }
    }
}

@Composable
fun WordListScreenNavTest(navController: NavHostController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Word List Screen")
    }
}

@Composable
fun GamePlayScreenNavTest(navController: NavHostController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Game Play Screen")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.navigate("gamelist") }) {
            Text("Back to Game List")
        }
    }
}


@Preview(showBackground = true)
@Composable
fun MyAppTestPreview() {
    B09_WQGATheme {
        MyAppTest()
    }
}

*/