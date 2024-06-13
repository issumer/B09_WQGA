/*
메인 화면 Scaffold의 Botton Navigation Bar에 해당하는 컴포넌트
*/

package com.example.b09_wqga.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.b09_wqga.navigation.Routes

@Composable
fun BottomNavigationBar(navController: NavController, userId: String) {
    NavigationBar {
        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = backStackEntry?.destination?.route

        NavBarItems.BarItems.forEach { navItem ->
            NavigationBarItem(
                selected = currentRoute == navItem.route,
                onClick = {
                    navController.navigate("${navItem.route}/$userId") {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (currentRoute == navItem.route) navItem.onSelectedIcon else navItem.selectIcon,
                        contentDescription = stringResource(id = navItem.titleId)
                    )
                },
                label = {
                    Text(text = stringResource(id = navItem.titleId))
                }
            )
        }
    }
}
