package com.example.b09_wqga.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.b09_wqga.ui.theme.pixelFont2

@Composable
fun Button_WQGA(width: Int, height: Int, text: String, onClickLabel : () -> Unit, enabled : Boolean = true){
    Box(
        modifier = Modifier
            .then(if (enabled) Modifier.clickable { onClickLabel() } else Modifier)
            .background(
                color = if (enabled) Color.Black else Color.Gray,
                shape = RoundedCornerShape((height / 2 - 6).dp)
            )
            .size(width = width.dp, height = height.dp),

        contentAlignment = Alignment.Center
    ) {
        androidx.compose.material3.Text(
            text = text,
            fontWeight = FontWeight.Black,
            fontFamily = pixelFont2,
            fontSize = 17.sp,
            color = Color.White
        )
    }
}
