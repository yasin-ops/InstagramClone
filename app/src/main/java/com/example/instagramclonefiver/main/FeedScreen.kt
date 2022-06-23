package com.example.instagramclonefiver.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.navigation.NavController
import com.example.instagramclonefiver.InstagramViewModel

@Composable
fun FeedScreen(navController: NavController, vm: InstagramViewModel) {
    Column(modifier = Modifier.fillMaxSize()) {
    Column(modifier = Modifier.weight(1f)) {
        Text(text = "FeedScreen")

    }
        BottomNavigationMenu(
        selectedItem = BottomNavigationItem.FEED,
        navController =navController )

    }
}