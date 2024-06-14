/*
BottomNavigationBar의 각 아이템에 해당하는 컴포넌트
*/

package com.example.b09_wqga.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FormatListNumberedRtl
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.VideogameAsset
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FormatListNumberedRtl
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.VideogameAsset
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource

import androidx.compose.ui.res.vectorResource


import com.example.b09_wqga.R


data class BarItem (val titleId :Int, val selectIcon: Int, val route:String)

object NavBarItems{
    val BarItems = listOf(
        BarItem(
            titleId = R.string.navBarItem_home,
            selectIcon = R.drawable.home,
            route = "HomeScreen"
        ),
        BarItem(
            titleId = R.string.navBarItem_vocabulary,
            selectIcon = R.drawable.voc,
            route = "VocListScreen"
        ),
        BarItem(
            titleId = R.string.navBarItem_gameList,
            selectIcon = R.drawable.game,
            route = "GameListScreen"
        ),
        BarItem(
            titleId = R.string.navBarItem_profile,
            selectIcon = R.drawable.accounticon,
            route = "ProfileScreen"
        )

    )
}
