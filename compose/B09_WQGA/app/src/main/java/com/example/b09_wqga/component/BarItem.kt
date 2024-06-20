package com.example.b09_wqga.component


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
