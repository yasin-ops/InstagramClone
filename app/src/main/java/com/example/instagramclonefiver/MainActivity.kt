package com.example.instagramclonefiver

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.instagramclonefiver.auth.LoginScreen
import com.example.instagramclonefiver.auth.ProfileScreen
import com.example.instagramclonefiver.auth.SignupScreen
import com.example.instagramclonefiver.data.PostData
import com.example.instagramclonefiver.main.*
import com.example.instagramclonefiver.ui.theme.InstagramCloneFiverTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            InstagramCloneFiverTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    InstagramApp()
                }
            }
        }
    }

}

// All Destination Classes
sealed class DestinationScreen(val route: String) {
    object Signup : DestinationScreen("singup")
    object Login : DestinationScreen("login")
    object Feed : DestinationScreen("feed")
    object Search : DestinationScreen("search")
    object MyPost : DestinationScreen("MyPost")
    object ProfileScreen : DestinationScreen("profileScreen")
    object NewPost : DestinationScreen("newpost/{imageUri}") {
        fun createRoute(uri: String) = "newpost/$uri"
    }
    object SinglePost:DestinationScreen("singlepost")




}

@Composable
fun InstagramApp() {
    // validate View Model Here
    val vm = hiltViewModel<InstagramViewModel>()
    val navController = rememberNavController()

    NotificationMessage(vm = vm)

    NavHost(navController = navController, startDestination = DestinationScreen.Signup.route) {
        composable(DestinationScreen.Signup.route){
            SignupScreen(naveController = navController,vm=vm)
        }
        composable(DestinationScreen.Login.route){
            LoginScreen(navController = navController,vm=vm)
        }
        composable(DestinationScreen.Feed.route){
            FeedScreen(navController = navController,vm=vm)
        }
        composable(DestinationScreen.Search.route){
            SearchScreen(navController = navController, Vm = vm)
        }

        composable(DestinationScreen.MyPost.route){
            MyPostScreen(navController = navController, vm = vm)
        }
        composable(DestinationScreen.NewPost.route) { navBackStachEntry ->
            val imageUri = navBackStachEntry.arguments?.getString("imageUri")
            imageUri?.let {
                NewPostScreen(navController =navController , vm =vm , encodeUri = it)
            }
        }

        composable(DestinationScreen.ProfileScreen.route){
            ProfileScreen(navController = navController, vm = vm)
        }
        composable(DestinationScreen.SinglePost.route){
          val postData=navController
              .previousBackStackEntry
              ?.arguments
              ?.getParcelable<PostData>("post")
            postData?.let { SinglePostScreen(navController = navController, vm = vm, post =postData ) }
        }


    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    InstagramCloneFiverTheme {
        InstagramApp()
    }
}