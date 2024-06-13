/*
구현 목록에서 홈 화면에 해당하는 화면
*/

package com.example.b09_wqga.screen

import android.widget.CalendarView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Architecture
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.b09_wqga.R
import com.example.b09_wqga.component.Button_WQGA
import com.example.b09_wqga.model.*
import com.example.b09_wqga.repository.AttendanceRepository
import com.example.b09_wqga.viewmodel.AttendanceViewModel
import com.example.b09_wqga.viewmodel.UserViewModel
import com.example.b09_wqga.viewmodelfactory.AttendanceViewModelFactory
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun HomeScreen(userId: String, userViewModel: UserViewModel) {
    val scrollState = rememberScrollState()
    val attendanceRepository = AttendanceRepository()
    val attendanceViewModel: AttendanceViewModel = viewModel(factory = AttendanceViewModelFactory(attendanceRepository))
    val userDataViewModel: UserDataViewModel = viewModel(viewModelStoreOwner = LocalNavGraphViewModelStoreOwner.current)
    val gameData: GameData? = userDataViewModel.getRecentlyPlayedGame()
    val wordData: WordData? = userDataViewModel.getRecentlyAddedWord()

    val currentDate = LocalDate.now()
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    var showDialog by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(currentDate) }
    var attendanceDates by remember { mutableStateOf(emptyList<String>()) }

    val points = userViewModel.points.value
    val username = userViewModel.username.value
    val lastAttendanceDate = userDataViewModel.getUserLastAttendanceDate()

    val isButtonEnabled = selectedDate == currentDate && (lastAttendanceDate == null || lastAttendanceDate != currentDate)

    LaunchedEffect(userId) {
        userViewModel.fetchUsername(userId)
        userViewModel.fetchPoints(userId)
        attendanceViewModel.getAttendanceDates(userId.toInt()) { dates ->
            attendanceDates = dates
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        Text(
            text = "Welcome back, $username!",
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
                    userViewModel.increasePoints(userId)
                    attendanceViewModel.addAttendance(userId.toInt(), currentDate.format(dateFormatter)) {
                        showDialog = true
                    }
                }, enabled = isButtonEnabled
            )
        }

        Calendar(
            currentDate = currentDate,
            selectedDate = selectedDate,
            onDateSelected = { date -> selectedDate = date },
            attendanceDates = attendanceDates,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 5.dp)
                .weight(1f)
        )

        if (gameData != null) {
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

        if (wordData != null) {
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
    attendanceDates: List<String>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val highlightedDates = attendanceDates.map { LocalDate.parse(it) }.toSet()

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            CalendarView(ctx).apply {
                setOnDateChangeListener { _, year, month, dayOfMonth ->
                    val date = LocalDate.of(year, month + 1, dayOfMonth)
                    onDateSelected(date)
                }
            }
        },
        update = { view ->
            for (date in highlightedDates) {
            }
        }
    )
}