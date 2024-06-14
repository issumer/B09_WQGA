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
import com.example.b09_wqga.database.Game
import com.example.b09_wqga.database.Played
import com.example.b09_wqga.database.Word
import com.example.b09_wqga.repository.AttendanceRepository
import com.example.b09_wqga.repository.GameRepository
import com.example.b09_wqga.repository.PlayedRepository
import com.example.b09_wqga.repository.VocRepository
import com.example.b09_wqga.ui.theme.pixelFont1
import com.example.b09_wqga.ui.theme.pixelFont2
import com.example.b09_wqga.viewmodel.AttendanceViewModel
import com.example.b09_wqga.viewmodel.GameViewModel
import com.example.b09_wqga.viewmodel.PlayedViewModel
import com.example.b09_wqga.viewmodel.UserViewModel
import com.example.b09_wqga.viewmodel.VocViewModel
import com.example.b09_wqga.viewmodelfactory.AttendanceViewModelFactory
import com.example.b09_wqga.viewmodelfactory.GameViewModelFactory
import com.example.b09_wqga.viewmodelfactory.PlayedViewModelFactory
import com.example.b09_wqga.viewmodelfactory.VocViewModelFactory
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

@Composable
fun HomeScreen(userId: String, userViewModel: UserViewModel) {
    val scrollState = rememberScrollState()
    val attendanceRepository = AttendanceRepository()
    val attendanceViewModel: AttendanceViewModel = viewModel(factory = AttendanceViewModelFactory(attendanceRepository))
    val gameRepository = GameRepository()
    val gameViewModel: GameViewModel = viewModel(factory = GameViewModelFactory(gameRepository))
    val playedRepository = PlayedRepository()
    val playedViewModel: PlayedViewModel = viewModel(factory = PlayedViewModelFactory(playedRepository))
    val vocRepository = VocRepository()
    val vocViewModel: VocViewModel = viewModel(factory = VocViewModelFactory(vocRepository))

    val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    var showCompleteDialog by remember { mutableStateOf(false) }
    var showFailDialog by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var attendanceDates by remember { mutableStateOf<List<String>>(listOf()) }
    var recentlyPlayed by remember { mutableStateOf<Played?>(null) }
    var wordData by remember { mutableStateOf<Word?>(null) }

    val points = userViewModel.points.value
    val name = userViewModel.name.value

    LaunchedEffect(userId) {
        userViewModel.fetchName(userId)
        userViewModel.fetchPoints(userId)

        val userIdInt = userId.toIntOrNull()
        if (userIdInt != null) {
            attendanceViewModel.getAttendanceDates(userIdInt) { dates ->
                attendanceDates = dates
            }

            playedViewModel.getAllPlayedByUserId(userIdInt) { playedList ->
                recentlyPlayed = playedList.maxByOrNull { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(it.play_date) }
            }

            vocViewModel.loadVocs(userIdInt)  // Correct method call
        }
    }

    val isButtonEnabled = !attendanceDates.contains(currentDate)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Welcome back, $name!",
                fontSize = 24.sp,
                fontFamily = pixelFont2,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(R.drawable.diamond),
                    contentDescription = "Points",
                    modifier = Modifier.size(30.dp)
                )
                Text(
                    text = "$points Points",
                    fontSize = 18.sp,
                    fontFamily = pixelFont1,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(start = 5.dp)
                )
            }
            Button_WQGA(width = 200, height = 40, text = "Attendance Check",
                onClickLabel = {
                    if (isButtonEnabled) {
                        val userIdInt = userId.toIntOrNull()
                        if (userIdInt != null) {
                            attendanceViewModel.addAttendance(userIdInt, currentDate) { success ->
                                if (success) {
                                    attendanceDates = attendanceDates + currentDate
                                    userViewModel.increasePoints(userId)
                                    showCompleteDialog = true
                                } else {
                                    showFailDialog = true
                                }
                            }
                        } else {
                            showFailDialog = true
                        }
                    } else {
                        showFailDialog = true
                    }
                }, enabled = isButtonEnabled
            )
        }

        Calendar(
            currentDate = selectedDate,
            selectedDate = selectedDate,
            onDateSelected = { date -> selectedDate = date },
            attendanceDates = attendanceDates,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 5.dp)
                .weight(1f)
        )

        if (recentlyPlayed != null) {
            Box(
                modifier = Modifier
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
            RecentlyPlayedGame(recentlyPlayed!!)
        }

        if (wordData != null) {
            Box(
                modifier = Modifier
                    .background(
                        color = Color.Black,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(start = 15.dp, end = 15.dp, top = 5.dp, bottom = 5.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Recently Added Word",
                    fontSize = 13.sp,
                    fontFamily = pixelFont1,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 5.dp)
                )
            }
            RecentlyAddedWord(wordData!!)
        }
    }

    if (showCompleteDialog) {
        AttendanceCompleteDialog(onDismiss = {
            showCompleteDialog = false
        }, onConfirm = {
            showCompleteDialog = false
        })
    }
    if (showFailDialog) {
        AttendanceFailDialog(onDismiss = {
            showFailDialog = false
        }, onConfirm = {
            showFailDialog = false
        })
    }
}

@Composable
fun RecentlyPlayedGame(played: Played) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)) {
        Text(
            text = "Game ID: ${played.game_id}",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground), // Replace with actual game image
                contentDescription = "Game Icon",
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                // Add game description and other details here
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Architecture, contentDescription = "Ranking Icon")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "${played.best_score}", fontSize = 16.sp) // Ranking

                    Spacer(modifier = Modifier.width(4.dp))

                    Icon(imageVector = Icons.Default.ArrowUpward, contentDescription = "Right Icon")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "${played.right}", fontSize = 16.sp) // Right

                    Spacer(modifier = Modifier.width(4.dp))

                    Icon(imageVector = Icons.Default.ArrowDownward, contentDescription = "Wrong Icon")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "${played.wrong}", fontSize = 16.sp) // Wrong
                }
            }
        }
    }
}

@Composable
fun RecentlyAddedWord(word: Word) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)) {
        Text(
            text = word.headword,
            fontSize = 22.sp,
            fontFamily = pixelFont2,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        word.meanings.forEach { meaning ->
            if (meaning.isNotEmpty()) {
                Text(
                    text = meaning,
                    fontSize = 18.sp,
                    fontFamily = pixelFont2,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
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
                // 이미 출석한 날짜 색칠 필요
            }
        }
    )
}

@Composable
fun AttendanceCompleteDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Attendance Completed!") },
        text = { Text("Good Luck~") },
        confirmButton = {
            Button_WQGA(width = 80, height = 40, text = "OK", onClickLabel = onConfirm)
        }
    )
}

@Composable
fun AttendanceFailDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Attendance Fail!") },
        text = { Text("You've already completed your attendance today! Please come back tomorrow.") },
        confirmButton = {
            Button_WQGA(width = 80, height = 40, text = "OK", onClickLabel = onConfirm)
        }
    )
}
