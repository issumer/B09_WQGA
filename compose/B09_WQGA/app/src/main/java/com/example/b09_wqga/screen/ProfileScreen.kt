/*
구현 목록에서 프로필 화면에 해당하는 화면
*/

package com.example.b09_wqga.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.VideogameAsset
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.b09_wqga.R

// 아직 로직(정보 가져오기 등)은 미구현
@Composable
fun ProfileScreen() {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
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
                Text(text = "ID: user123", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(text = "Name: John Doe", fontSize = 16.sp)
            }
            Button(onClick = {  }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.Logout, contentDescription = "Log out")
                Spacer(modifier = Modifier.width(4.dp))
                Text("Log out")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Information
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

        // Settings
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