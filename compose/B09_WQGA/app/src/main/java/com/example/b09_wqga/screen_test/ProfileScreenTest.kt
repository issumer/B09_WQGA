package com.example.b09_wqga.screen_test

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Abc
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Logout
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextGranularity.Companion.Word
import com.example.b09_wqga.R
import com.example.b09_wqga.ui.theme.B09_WQGATheme

@Composable
fun ProfileScreenTest(onLogOutClick: () -> Unit) {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        // Profile section
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
                Text(text = "ID: user123", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(text = "Name: John Doe", fontSize = 16.sp)
            }
            Button(onClick = onLogOutClick) {
                Icon(imageVector = Icons.AutoMirrored.Filled.Logout, contentDescription = "Log out")
                Spacer(modifier = Modifier.width(4.dp))
                Text("Log out")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Information section
        Text(text = "Information", fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))

        val infoList = listOf(
            "Registered date: 01/01/2022",
            "Points: 1234",
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

        // Settings section
        Text(text = "Settings", fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))

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

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    B09_WQGATheme {
        ProfileScreenTest(onLogOutClick = { /*TODO*/ })
    }
}