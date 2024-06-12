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
import androidx.compose.ui.res.stringResource
import com.example.b09_wqga.R

data class BarItem (val titleId :Int, val selectIcon: ImageVector, val onSelectedIcon :ImageVector, val route:String)

object NavBarItems{
    val BarItems = listOf(
        BarItem(
            titleId = R.string.navBarItem_home,
            selectIcon = Icons.Default.Home,
            onSelectedIcon = Icons.Outlined.Home,
            route = "HomeScreen"
        ),
        BarItem(
            titleId = R.string.navBarItem_vocabulary,
            selectIcon = Icons.Default.FormatListNumberedRtl,
            onSelectedIcon = Icons.Outlined.FormatListNumberedRtl,
            route = "VocListScreen"
        ),
        BarItem(
            titleId = R.string.navBarItem_gameList,
            selectIcon = Icons.Default.VideogameAsset,
            onSelectedIcon = Icons.Outlined.VideogameAsset,
            route = "GameListScreen"
        ),
        BarItem(
            titleId = R.string.navBarItem_profile,
            selectIcon = Icons.Default.Person,
            onSelectedIcon = Icons.Outlined.Person,
            route = "ProfileScreen"
        )

    )
}