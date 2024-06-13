/*
구현 목록에서 처음 실행 화면에 해당하는 화면
*/

package com.example.b09_wqga.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.b09_wqga.R
import com.example.b09_wqga.navigation.Routes

@Composable
fun InitialScreen(navController: NavHostController) {
    Column(modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {

        Image(painter = painterResource(id = R.drawable.logo),
            contentDescription = "logo",
            modifier = Modifier.size(200.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Image(painter = painterResource(id = R.drawable.loginbutton),
            contentDescription = "logo",
            modifier = Modifier.size(width = 100.dp, height = 41.dp)
                .clickable { navController.navigate(Routes.LoginScreen.route) }
        )



        Spacer(modifier = Modifier.height(30.dp))


        Image(painter = painterResource(id = R.drawable.joinbutton),
            contentDescription = "logo",
            modifier = Modifier.size(width = 100.dp, height = 41.dp)
                .clickable { navController.navigate(Routes.RegisterScreen.route) }
        )

    }
}
