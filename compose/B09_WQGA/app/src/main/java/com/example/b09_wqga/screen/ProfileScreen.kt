package com.example.b09_wqga.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.b09_wqga.R
import com.example.b09_wqga.model.UserDataViewModel
import com.example.b09_wqga.ui.theme.nanumFontFamily
import com.example.b09_wqga.ui.theme.pixelFont1
import com.example.b09_wqga.ui.theme.pixelFont2
import com.example.b09_wqga.viewmodel.UserViewModel

@Composable
fun ProfileScreen(userId: String, userViewModel: UserViewModel, navController: NavHostController) {
    val context = LocalContext.current
    val userDataViewModel: UserDataViewModel = viewModel(viewModelStoreOwner = LocalNavGraphViewModelStoreOwner.current)

    // Fetch user data when the ProfileScreen is composed
    LaunchedEffect(userId) {
        userViewModel.fetchName(userId)
        userViewModel.fetchUsername(userId)
        userViewModel.fetchPoints(userId)
        userViewModel.fetchEnterDate(userId)
    }

    val name by remember { userViewModel.name }
    val username by remember { userViewModel.username }
    val points by remember { userViewModel.points }
    val enterdate by remember { userViewModel.enterdate }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Profile
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = painterResource(id = R.drawable.accounticon),
                contentDescription = "Profile Picture",
                modifier = Modifier.size(50.dp)
            )
            Column {
                Text(text = "ID: $username", fontSize = 20.sp, fontFamily = pixelFont2, fontWeight = FontWeight.ExtraBold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Name: $name", fontSize = 20.sp,fontFamily = pixelFont2, fontWeight = FontWeight.ExtraBold)
            }
            TextButton(onClick = {
                userViewModel.logout(context) {
                    navController.navigate("InitialScreen") {
                        userDataViewModel.showBottomNavigationBar.value = false
                        popUpTo(navController.graph.id) {// 백스택 모두 지우기
                            inclusive = true
                        }
                    }
                }
            },
                modifier = Modifier
                    .background(
                        color = Color.Black,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(start = 5.dp, end = 5.dp)) {
                Icon(painter = painterResource(id = R.drawable.logout), contentDescription = "Log out",
                modifier = Modifier.size(20.dp), tint = Color.White
                    )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Log out", fontFamily = pixelFont1, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Information
        Text(
            text = "Information",
            fontSize = 24.sp,
            fontFamily = pixelFont1,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        val infoList = listOf(
            "Registered date: $enterdate",
            "Points: $points",
            "Total word count: ${userDataViewModel.getTotalWordCount()}",
            "Total right count: ${userDataViewModel.getTotalRightCount()}",
            "Total wrong count: ${userDataViewModel.getTotalWrongCount()}",
            "Total played games: ${userDataViewModel.getTotalPlayedGames()}"
        )

        infoList.forEach { info ->
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 8.dp)) {
                Icon(imageVector = Icons.Default.Info, contentDescription = "Info Icon", modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = info, fontSize = 16.sp, fontFamily = pixelFont2)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Settings
//        Text(
//            text = "Settings",
//            fontSize = 24.sp,
//            fontWeight = FontWeight.Bold,
//            modifier = Modifier.padding(bottom = 16.dp)
//        )
//
//        LazyColumn {
//            items(listOf("Setting 1", "Setting 2", "Setting 3", "Setting 4")) { setting ->
//                Text(
//                    text = setting,
//                    fontSize = 18.sp,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(vertical = 8.dp)
//                        .padding(start = 8.dp)
//                )
//            }
//        }
    }
}
