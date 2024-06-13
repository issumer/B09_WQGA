package com.example.b09_wqga.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.b09_wqga.R
import com.example.b09_wqga.viewmodel.UserViewModel

@Composable
fun ProfileScreen(userId: String, userViewModel: UserViewModel, navController: NavController) {
    val context = LocalContext.current

    // Fetch user data when the ProfileScreen is composed
    LaunchedEffect(userId) {
        userViewModel.fetchUsername(userId)
        userViewModel.fetchPoints(userId)
        userViewModel.fetchEnterDate(userId)
    }

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
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "Profile Picture",
                modifier = Modifier.size(64.dp)
            )
            Column {
                Text(text = "ID: $userId", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(text = "Name: $username", fontSize = 16.sp)
            }
            Button(onClick = {
                userViewModel.logout(context) {
                    navController.navigate("LoginScreen") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.Logout, contentDescription = "Log out")
                Spacer(modifier = Modifier.width(4.dp))
                Text("Log out")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Information
        Text(
            text = "Information",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        val infoList = listOf(
            "Registered date: $enterdate",
            "Points: $points",
            "Total word count: 567",
            "Total right count: 456",
            "Total wrong count: 111",
            "Total played games: 99"
        )

        infoList.forEach { info ->
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 8.dp)) {
                Icon(imageVector = Icons.Default.Info, contentDescription = "Info Icon", modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = info, fontSize = 16.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Settings
        Text(
            text = "Settings",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn {
            items(listOf("Setting 1", "Setting 2", "Setting 3", "Setting 4")) { setting ->
                Text(
                    text = setting,
                    fontSize = 18.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .padding(start = 8.dp)
                )
            }
        }
    }
}
