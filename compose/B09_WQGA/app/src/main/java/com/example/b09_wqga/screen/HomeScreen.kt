/*
구현 목록에서 홈 화면에 해당하는 화면
*/

package com.example.b09_wqga.screen

import android.os.Bundle
import android.widget.CalendarView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Architecture
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Score
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.b09_wqga.R
import com.example.b09_wqga.component.Button_WQGA
import com.example.b09_wqga.model.GameData
import com.example.b09_wqga.model.UserDataViewModel
import com.example.b09_wqga.model.WordData
import com.example.b09_wqga.ui.theme.B09_WQGATheme
import java.time.LocalDate


@Composable
fun HomeScreen() {
    val userDataViewModel: UserDataViewModel = viewModel(viewModelStoreOwner = LocalNavGraphViewModelStoreOwner.current)
    val scrollState = rememberScrollState()
    val gameData: GameData? = userDataViewModel.getRecentlyPlayedGame() // 최근에 플레이한 게임
    val wordData: WordData? = userDataViewModel.getRecentlyAddedWord() // 최근에 생성한 단어

    val currentDate = LocalDate.now()

    var showDialog by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(currentDate) }

    val points = userDataViewModel.getUserPoints()
    val lastAttendanceDate = userDataViewModel.getUserLastAttendanceDate()

    val isButtonEnabled = selectedDate == currentDate && (lastAttendanceDate == null || lastAttendanceDate != currentDate)


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        Text(
            text = "Welcome back, ${userDataViewModel.userId.value}!",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Diamond,
                    contentDescription = "Points",
                    modifier = Modifier.size(24.dp)
                )
                Text(text = "$points Points", fontSize = 18.sp)
            }
            Button_WQGA(width = 200, height = 40, text = "Attendance Check",
                onClickLabel = {
                    userDataViewModel.increasePoints()
                    showDialog = true
                }, enabled = isButtonEnabled
            )
        }

        Calendar(
            currentDate = currentDate,
            selectedDate = selectedDate,
            onDateSelected = { date -> selectedDate = date },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 5.dp)
                .weight(1f)
        )

        if(gameData != null) {
            Box(modifier = Modifier
                .background(
                    color = colorResource(id = R.color.wqga).copy(alpha = 0.7f),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(start = 8.dp, end = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Recently Played Game",
                    fontSize = 18.sp,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 5.dp)
                )
            }
            RecentlyPlayedGame(gameData)
        }

        if(wordData != null) {
            Box(modifier = Modifier
                .background(
                    color = colorResource(id = R.color.wqga).copy(alpha = 0.7f),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(start = 8.dp, end = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Recently Added Word",
                    fontSize = 18.sp,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 5.dp)
                )
            }
            RecentlyAddedWord(wordData)
        }
    }


    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "Attendance Completed!") },
            text = { Text("Good Luck~") },
            confirmButton = {
                Button_WQGA(width = 80, height = 40, text = "check", onClickLabel = { showDialog = false })
            }
        )
    }
}

@Composable
fun RecentlyPlayedGame(gameData: GameData) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)) {
        Text(
            text = gameData.title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = gameData.imageResource),
                contentDescription = "Game Icon",
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = gameData.description,
                    fontSize = 16.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Architecture, contentDescription = "Ranking Icon")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "${gameData.userRanking}", fontSize = 16.sp) // Ranking

                    Spacer(modifier = Modifier.width(4.dp))

                    Icon(imageVector = Icons.Default.ArrowUpward, contentDescription = "Right Icon")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "${gameData.userRight}", fontSize = 16.sp) // Right

                    Spacer(modifier = Modifier.width(4.dp))

                    Icon(imageVector = Icons.Default.ArrowDownward, contentDescription = "Wrong Icon")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "${gameData.userWrong}", fontSize = 16.sp) // Wrong
                }
            }
        }
    }
}

@Composable
fun RecentlyAddedWord(word: WordData) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)) {
        Text(
            text = word.headword,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        word.meanings.forEach { meaning ->
            if(!meaning.isEmpty()) {
                Text(text = meaning, fontSize = 16.sp, modifier = Modifier.padding(bottom = 4.dp))
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.ArrowUpward, contentDescription = "Icon")
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "${word.right}")
                Spacer(modifier = Modifier.width(8.dp))
                Icon(imageVector = Icons.Default.ArrowDownward, contentDescription = "Icon")
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "${word.wrong}")
            }
        }
    }
}

@Composable
fun Calendar(
    currentDate: LocalDate,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            CalendarView(ctx).apply {
                setOnDateChangeListener { _, year, month, dayOfMonth ->
                    val date = LocalDate.of(year, month + 1, dayOfMonth)
                    onDateSelected(date)
                }
            }
        }
    )
}
