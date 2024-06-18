package com.example.b09_wqga.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.b09_wqga.R
import com.example.b09_wqga.component.Button_WQGA
import com.example.b09_wqga.model.UserDataViewModel
import com.example.b09_wqga.repository.PlayedRepository
import com.example.b09_wqga.repository.UserRepository
import com.example.b09_wqga.repository.VocRepository
import com.example.b09_wqga.ui.theme.pixelFont1
import com.example.b09_wqga.ui.theme.pixelFont2
import com.example.b09_wqga.viewmodel.PlayedViewModel
import com.example.b09_wqga.viewmodel.UserViewModel
import com.example.b09_wqga.viewmodel.VocViewModel
import com.example.b09_wqga.viewmodelfactory.PlayedViewModelFactory
import com.example.b09_wqga.viewmodelfactory.UserViewModelFactory
import com.example.b09_wqga.viewmodelfactory.VocViewModelFactory

@Composable
fun ProfileScreen(navController: NavHostController, userId: Int) {
    val context = LocalContext.current
    val userDataViewModel: UserDataViewModel = viewModel(viewModelStoreOwner = LocalNavGraphViewModelStoreOwner.current)
    val userRepository = UserRepository()
    val userViewModel: UserViewModel = viewModel(factory = UserViewModelFactory(userRepository))
    val vocRepository = VocRepository()
    val vocViewModel: VocViewModel = viewModel(factory = VocViewModelFactory(vocRepository))
    val playedRepository = PlayedRepository()
    val playedViewModel: PlayedViewModel = viewModel(factory = PlayedViewModelFactory(playedRepository))

    // Fetch user data when the ProfileScreen is composed
    LaunchedEffect(userId) {
        userViewModel.fetchName(userId.toString())
        userViewModel.fetchUsername(userId.toString())
        userViewModel.fetchPoints(userId.toString())
        userViewModel.fetchEnterDate(userId.toString())
        vocViewModel.loadVocs(userId)
        playedViewModel.loadPlayedByUserId(userId)
    }

    val name by remember { userViewModel.name }
    val username by remember { userViewModel.username }
    val points by remember { userViewModel.points }
    val enterdate by remember { userViewModel.enterdate }
    var showChangeNameDialog by rememberSaveable {
        mutableStateOf(false)
    }

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
                Text(
                    text = "ID: $username",
                    fontSize = 20.sp,
                    fontFamily = pixelFont2,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Name: $name",
                    fontSize = 20.sp,
                    fontFamily = pixelFont2,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.clickable {
                        showChangeNameDialog = true
                    }
                )
            }
            TextButton(
                onClick = {
                    userViewModel.logout(context) {
                        navController.navigate("InitialScreen") {
                            userDataViewModel.showBottomNavigationBar.value = false
                            popUpTo(navController.graph.id) { // 백스택 모두 지우기
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
                    .padding(start = 5.dp, end = 5.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.logout),
                    contentDescription = "Log out",
                    modifier = Modifier.size(20.dp),
                    tint = Color.White
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
            "Total word count: ${vocViewModel.getTotalWordCount()}",
            "Total right count: ${vocViewModel.getTotalRightCount()}",
            "Total wrong count: ${vocViewModel.getTotalWrongCount()}",
            "Total played games: ${playedViewModel.getTotalPlayedGames()}"
        )

        infoList.forEach { info ->
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 8.dp)) {
                Icon(imageVector = Icons.Default.Info, contentDescription = "Info Icon", modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = info, fontSize = 16.sp, fontFamily = pixelFont2)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
    if (showChangeNameDialog) {
        NameEditDialog(
            onDismiss = { showChangeNameDialog = false },
            name = name,
            onSave = { saveName ->
                userViewModel.updateUserName(userId.toString(), saveName)
                showChangeNameDialog = false
            }
        )
    }
}

@Composable
fun NameEditDialog(onDismiss: () -> Unit, name: String, onSave: (String) -> Unit) {
    var newName by remember {
        mutableStateOf(name)
    }
    var warningMessage by rememberSaveable {
        mutableStateOf("")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Change Name",
                fontFamily = pixelFont1,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Text(text = warningMessage)
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Button_WQGA(width = 80, height = 40, text = "Save",
                    onClickLabel = {
                        if (newName.isEmpty()) {
                            warningMessage = "Name을 채워주세요!"
                        } else {
                            warningMessage = ""
                            onSave(newName)
                        }
                    },
                    enabled = true
                )
            }
        }
    )
}
