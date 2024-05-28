package com.example.b09_wqga.screen_test

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Abc
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Language
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextGranularity.Companion.Word
import com.example.b09_wqga.R
import com.example.b09_wqga.ui.theme.B09_WQGATheme

@Composable
fun GamePlayScreenTest(onMenuClick: () -> Unit, onSkillClick: (String) -> Unit, onSubmitClick: () -> Unit) {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .weight(1f)) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Draw your game play canvas here
            }
            Button(onClick = onMenuClick, modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)) {
                Text("Menu")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        SkillBarTest(onSkillClick = onSkillClick)

        Spacer(modifier = Modifier.height(16.dp))

        WordQuizTest(onSubmitClick = onSubmitClick)
    }
}

@Composable
fun SkillBarTest(onSkillClick: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(onClick = { onSkillClick("Attack") }) {
            Text("Attack")
        }
        Button(onClick = { onSkillClick("Defend") }) {
            Text("Defend")
        }
        Button(onClick = { onSkillClick("Run") }) {
            Text("Run")
        }
    }
}

@Composable
fun WordQuizTest(onSubmitClick: () -> Unit) {
    var selectedOption by remember { mutableStateOf("") }
    val options = listOf("Selection 1", "Selection 2", "Selection 3", "Selection 4", "Selection 5")

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = "Quiz Question", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))

        options.forEach { option ->
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                RadioButton(
                    selected = selectedOption == option,
                    onClick = { selectedOption = option }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = option, fontSize = 16.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Answer or Wrong", fontSize = 16.sp, color = Color.Red, modifier = Modifier.padding(bottom = 16.dp))

        Button(onClick = onSubmitClick, modifier = Modifier.align(Alignment.End)) {
            Text("Submit")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GamePlayScreenTestPreview() {
    B09_WQGATheme {
        GamePlayScreenTest(
            onMenuClick = { /*TODO*/ },
            onSkillClick = { /*TODO*/ },
            onSubmitClick = { /*TODO*/ }
        )
    }
}