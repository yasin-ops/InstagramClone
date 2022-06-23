package com.example.instagramclonefiver.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.instagramclonefiver.DestinationScreen
import com.example.instagramclonefiver.R

enum class BottomNavigationItem (val Icons:Int, val navDestination:DestinationScreen){
    FEED(R.drawable.ic_posts,DestinationScreen.Feed),
    SEARCH(R.drawable.ic_search, DestinationScreen.Search),
    POST(R.drawable.ic_home,DestinationScreen.MyPost)
}
@Composable
fun BottomNavigationMenu(selectedItem:BottomNavigationItem,navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 4.dp)
            .background(Color.White)

    ) {
        for(item in BottomNavigationItem.values()){
            Image(painter = painterResource(id = item.Icons)
                , contentDescription =null,
                modifier = Modifier.size(40.dp)
                    .padding(5.dp)
                    .weight(1f).clickable {
                        navigateTO(navController,item.navDestination)
                    },
                colorFilter = if(item==selectedItem)ColorFilter.tint(Color.Blue)
                else ColorFilter.tint(Color.Gray)
            )

        }
    }

}