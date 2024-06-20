package com.example.b09_wqga.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Architecture
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import java.time.YearMonth
import java.time.format.TextStyle
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
    var wordData by remember { mutableStateOf<Word?>(null) }

    val points = userViewModel.points.value
    val name = userViewModel.name.value

    var recentlyPlayed by remember { mutableStateOf<Played?>(null) }
    var recentlyPlayedGame by remember { mutableStateOf<Game?>(null) }

    val vocList by vocViewModel.vocList.collectAsState()
    var recentWord by remember { mutableStateOf<Word?>(null) }

    LaunchedEffect(userId) {
        val userIdInt = userId.toIntOrNull()
        if (userIdInt != null) {
            vocViewModel.loadVocs(userIdInt)
        }
    }
    LaunchedEffect(vocList) {
        if (vocList.isNotEmpty()) {
            recentWord = vocList.flatMap { it.words_json }
                .maxByOrNull { SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(it.create_date) }
        }
    }

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
                recentlyPlayed?.let { played ->
                    gameViewModel.getGameById(played.game_id) { game ->
                        recentlyPlayedGame = game
                    }
                }
            }

            vocViewModel.loadVocs(userIdInt)
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
        Spacer(modifier = Modifier.height(100.dp))
        CustomCalendar(
            selectedDate = selectedDate,
            onDateSelected = { date -> selectedDate = date },
            attendanceDates = attendanceDates,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 5.dp)
                .weight(1f)
        )

        if (recentlyPlayed != null && recentlyPlayedGame != null) {
            Box(
                modifier = Modifier
                    .background(
                        color = Color.Black,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(start = 15.dp, end = 15.dp, top = 10.dp, bottom = 5.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Recently Played Game",
                    fontSize = 20.sp,
                    fontFamily = pixelFont2,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 5.dp)
                )
            }
            RecentlyPlayedGame(recentlyPlayed!!, recentlyPlayedGame!!.gamename)
        }

        if (recentWord != null) {
            Box(
                modifier = Modifier
                    .background(
                        color = Color.Black,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(start = 15.dp, end = 15.dp, top = 10.dp, bottom = 5.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Recently Added Word",
                    fontSize = 20.sp,
                    fontFamily = pixelFont2,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 5.dp)
                )
            }
            RecentlyAddedWord(recentWord!!)
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
fun CustomCalendar(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    attendanceDates: List<String>,
    modifier: Modifier = Modifier
) {
    val yearMonth = YearMonth.now()
    val daysInMonth = yearMonth.lengthOfMonth()
    val firstDayOfWeek = yearMonth.atDay(1).dayOfWeek.value % 7 // 1=Monday ... 7=Sunday, so % 7 to make it 0=Sunday ... 6=Saturday
    val attendanceDatesSet = attendanceDates.map { LocalDate.parse(it) }.toSet()

    Column(modifier = modifier) {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "${yearMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${yearMonth.year}",
                fontSize = 20.sp,
                fontFamily = pixelFont2,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    fontWeight = FontWeight.Bold,
                    fontFamily = pixelFont2,
                    fontSize = 16.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }

        Column {
            var dayOfMonth = 1
            while (dayOfMonth <= daysInMonth) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    for (i in 0..6) {
                        if ((dayOfMonth == 1 && i < firstDayOfWeek) || dayOfMonth > daysInMonth) {
                            Box(modifier = Modifier.weight(1f)) {} // empty box for days not in month
                        } else {
                            val date = yearMonth.atDay(dayOfMonth)
                            val isSelected = date == selectedDate
                            val isAttendanceDay = attendanceDatesSet.contains(date)

                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(4.dp)
                                    .background(
                                        color = when {
                                            isSelected -> Color.Black
                                            isAttendanceDay -> Color.Gray
                                            else -> Color.Transparent
                                        },
                                        shape = RoundedCornerShape(4.dp)
                                    )
                                    .clickable {
                                        onDateSelected(date)
                                    }
                            ) {
                                Text(
                                    text = dayOfMonth.toString(),
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontFamily = pixelFont2,
                                    color = if (isSelected || isAttendanceDay) Color.White else Color.Black
                                )
                            }
                            dayOfMonth++
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RecentlyPlayedGame(played: Played, gameName: String) {
    val gameImageResource = when (played.game_id) {
        1 -> R.drawable.game1
        2 -> R.drawable.game2
        else -> R.drawable.ic_launcher_foreground
    }
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)) {

        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = gameImageResource),
                contentDescription = "Game Icon",
                modifier = Modifier.size(48.dp)

            )
            Column(){
                Text(
                    text = "Game: $gameName",
                    fontSize = 20.sp,
                    fontFamily = pixelFont2,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 4.dp, start = 10.dp)
                )
                Row(verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(4.dp)) {
                    Icon(imageVector = Icons.Default.Architecture, contentDescription = "Ranking Icon")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "${played.best_score}",fontFamily = pixelFont2, fontSize = 16.sp) // Ranking
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(imageVector = Icons.Default.ArrowUpward, contentDescription = "Right Icon")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "${played.right}",fontFamily = pixelFont2, fontSize = 16.sp) // Right
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(imageVector = Icons.Default.ArrowDownward, contentDescription = "Wrong Icon")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "${played.wrong}",fontFamily = pixelFont2, fontSize = 16.sp) // Wrong
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
                Text(text = "${word.right}",fontFamily = pixelFont2)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(imageVector = Icons.Default.ArrowDownward, contentDescription = "Icon")
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "${word.wrong}",fontFamily = pixelFont2)
            }
        }
    }
}

@Composable
fun AttendanceCompleteDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Attendance Completed!",fontFamily = pixelFont2,) },
        text = { Text("Good Luck~",fontFamily = pixelFont2,) },
        confirmButton = {
            Button_WQGA(width = 80, height = 40, text = "OK", onClickLabel = onConfirm)
        }
    )
}

@Composable
fun AttendanceFailDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Attendance Fail!",fontFamily = pixelFont2,) },
        text = { Text("You've already completed your attendance today! Please come back tomorrow.",fontFamily = pixelFont2,) },
        confirmButton = {
            Button_WQGA(width = 80, height = 40, text = "OK", onClickLabel = onConfirm)
        }
    )
}
