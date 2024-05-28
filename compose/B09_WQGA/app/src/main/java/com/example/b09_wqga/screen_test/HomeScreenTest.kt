package com.example.b09_wqga.screen_test

import android.os.Bundle
import android.widget.CalendarView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Architecture
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.b09_wqga.R
import com.example.b09_wqga.ui.theme.B09_WQGATheme

@Composable
fun HomeScreenTest() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Welcome back, <ID>!",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        RecentlyPlayedGameTest(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        RecentlyAddedWordTest(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        Text(
            text = "Attendance",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        CalendarTest(
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
fun RecentlyPlayedGameTest(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = "Recently Played Game",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Game Title",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "Game Icon",
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Column {
                Text(text = "Game Description", fontSize = 16.sp)
                Row {
                    Icon(
                        imageVector = Icons.Default.Architecture,
                        contentDescription = "Icon",
                        modifier = Modifier.size(24.dp)
                    )
                    Text(text = "Ranking", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowUpward,
                        contentDescription = "Icon",
                        modifier = Modifier.size(24.dp)
                    )
                    Text(text = "Right", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowDownward,
                        contentDescription = "Icon",
                        modifier = Modifier.size(24.dp)
                    )
                    Text(text = "Wrong", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Button(onClick = { /*TODO*/ }) {
                        Text("Play")
                    }
                }
            }
        }
    }
}

@Composable
fun RecentlyAddedWordTest(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = "Recently Added Word",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Headword",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        for (i in 1..5) {
            Text(text = "Meaning $i", fontSize = 16.sp, modifier = Modifier.padding(bottom = 4.dp))
        }
    }
}

@Composable
fun CalendarTest(modifier: Modifier = Modifier) {
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
fun HomeScreenTestPreview() {
    B09_WQGATheme {
        HomeScreenTest()
    }
}
