package com.example.instagramclonefiver.main


import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.NavController
import com.example.instagramclonefiver.InstagramViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.modifier.ModifierLocalReadScope
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.instagramclonefiver.DestinationScreen
import com.example.instagramclonefiver.ui.theme.Shapes

@Composable
fun SearchScreen(vm: InstagramViewModel, navController: NavController) {
    val searchedPostsLoading = vm.serachedPostsProgress.value
    val searchedPosts = vm.searchedPost.value
    var searchTerms by rememberSaveable { mutableStateOf("") }
    Column() {
        SearchBar(
            searchTerms = searchTerms,
            onSearchChange = { searchTerms = it },
            onSearch = { vm.searchedPost(searchTerms) }
        )
        PostList(
            isContextLoading = false, postsLoading = searchedPostsLoading, posts = searchedPosts,
            modifier = Modifier
                .weight(1f)
                .padding(8.dp)
        ) { post ->
            navigateTO(
                navController = navController,
                dest = DestinationScreen.SinglePost,
                NavParam("post", post)
            )
        }
        BottomNavigationMenu(
            selectedItem = BottomNavigationItem.SEARCH,
            navController = navController
        )

    }
}

@Composable
fun SearchBar(searchTerms: String, onSearchChange: (String) -> Unit, onSearch: () -> Unit) {
val focusManager= LocalFocusManager.current
    TextField(value = searchTerms,
        onValueChange = onSearchChange,
    modifier = Modifier
        .padding(8.dp)
        .fillMaxWidth()
        .border(1.dp, color = Color.LightGray, CircleShape),
        shape = CircleShape,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(onSearch={
            onSearch()
            focusManager.clearFocus()
        }
        ),
        maxLines = 1,
        singleLine = true,
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.Transparent,
            textColor = Color.Black,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent

        ),
        trailingIcon = {
            IconButton(onClick = {
                onSearch()
                focusManager.clearFocus()
            }) {

                Icon(imageVector = Icons.Filled.Search, contentDescription = null)
            }
        }
    )

}

