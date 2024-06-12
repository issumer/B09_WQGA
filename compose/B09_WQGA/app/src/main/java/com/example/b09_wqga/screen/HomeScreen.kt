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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Architecture
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Score
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.b09_wqga.R
import com.example.b09_wqga.model.GameData
import com.example.b09_wqga.model.UserDataViewModel
import com.example.b09_wqga.model.WordData
import com.example.b09_wqga.ui.theme.B09_WQGATheme


@Composable
fun HomeScreen() {
    val userDataViewModel: UserDataViewModel = viewModel(viewModelStoreOwner = LocalNavGraphViewModelStoreOwner.current)
    val scrollState = rememberScrollState()
    val gameData: GameData? = userDataViewModel.getRecentlyPlayedGame() // 최근에 플레이한 게임
    val wordData: WordData? = userDataViewModel.getRecentlyAddedWord() // 최근에 생성한 단어

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

        if(gameData != null) {
            Text(
                text = "Recently Played Game",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            RecentlyPlayedGame(gameData)
        }

        if(wordData != null) {
            Text(
                text = "Recently Added Word",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            RecentlyAddedWord(wordData)
        }

        Text(
            text = "Attendance",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Calendar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .weight(1f)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = { /*TODO*/ }) {
                Text("Attendance Check")
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Score,
                    contentDescription = "Points",
                    modifier = Modifier.size(24.dp)
                )
                Text(text = "Points", fontSize = 18.sp)
            }
        }
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
fun Calendar(modifier: Modifier = Modifier) {
    val month = 5
    val year = 2024
    val today = 27
    val daysInMonth = getDaysInMonth(month, year)
    Box(
        modifier = modifier
            .background(Color.Gray)
            .fillMaxSize()
    ) {
        LazyVerticalGrid(columns = GridCells.Fixed(7)) {
            items(daysInMonth) { day ->
                val actualDay = day + 1
                if(actualDay == today) {
                    Text(
                        modifier = Modifier.background(color = Color.Blue),
                        text = actualDay.toString()
                    )
                } else {
                    Text(actualDay.toString())
                }

            }
        }
    }
}

// Function to get the number of days in a month
fun getDaysInMonth(month: Int, year: Int): Int {
    return when (month) {
        1, 3, 5, 7, 8, 10, 12 -> 31
        4, 6, 9, 11 -> 30
        else -> if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0) 29 else 28
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    B09_WQGATheme {
        HomeScreen()
    }
}
