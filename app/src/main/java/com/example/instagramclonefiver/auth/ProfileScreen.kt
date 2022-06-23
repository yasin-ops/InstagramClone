package com.example.instagramclonefiver.auth

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.instagramclonefiver.DestinationScreen
import com.example.instagramclonefiver.InstagramViewModel
import com.example.instagramclonefiver.main.CommonDivider
import com.example.instagramclonefiver.main.CommonImage
import com.example.instagramclonefiver.main.CommonProgressSpinner
import com.example.instagramclonefiver.main.navigateTO

@Composable
fun ProfileScreen(navController: NavController, vm: InstagramViewModel) {
    val isLoading = vm.inProgress.value
    if (isLoading) {
        CommonProgressSpinner()
    } else {
        val userData = vm.userData.value
        var name by rememberSaveable { mutableStateOf(userData?.name ?: "") }
        var username by rememberSaveable { mutableStateOf(userData?.userName ?: "") }
        var bio by rememberSaveable { mutableStateOf(userData?.bio ?: "") }
        ProfileContent(vm = vm,
            name = name,
            username = username,
            bio = bio,
            onNameChange = { name = it },
            onUsernameChange = { username = it },
            onBioChange = { bio = it },
            OnSave = {vm.updateProfileData(name,username,bio)},
            OnBack = { navigateTO(navController = navController, DestinationScreen.MyPost) },
            OnLogout = {
                vm.onLogout()
                navigateTO(navController,DestinationScreen.Login)
            })


    }


}

@Composable
fun ProfileContent(
    vm: InstagramViewModel,
    name: String,
    username: String,
    bio: String,
    onNameChange: (String) -> Unit,
    onUsernameChange: (String) -> Unit,
    onBioChange: (String) -> Unit,
    OnSave: () -> Unit,
    OnBack: () -> Unit,
    OnLogout: () -> Unit
) {
    val scrollState = rememberScrollState()
    val imageUrl = vm.userData?.value?.imageUrl
    Column(
        modifier = Modifier
            .verticalScroll(scrollState)
            .padding(8.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Back", modifier = Modifier.clickable { OnBack.invoke() })
            Text(text = "Save", modifier = Modifier.clickable { OnSave.invoke() })

        }
        CommonDivider()
        ProfileImage(ImageUrl = imageUrl, vm =vm )

        CommonDivider()


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .background(Color.Gray)
        ) {


        }
        CommonDivider()
        Row(
            modifier = Modifier.padding(start = 4.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Name", modifier = Modifier.width(100.dp))
            TextField(
                value = name,
                onValueChange = onNameChange,
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent,
                    textColor = Color.Blue
                )
            )

        }
        Row(
            modifier = Modifier.padding(start = 4.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "User Name", modifier = Modifier.width(100.dp))
            TextField(
                value = username,
                onValueChange = onNameChange,
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent,
                    textColor = Color.Blue
                )
            )

        }
        Row(
            modifier = Modifier.padding(start = 4.dp, end = 4.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text(text = bio, modifier = Modifier.width(100.dp))
            TextField(
                value = bio,
                onValueChange = onNameChange,
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent,
                    textColor = Color.Blue
                ),
                singleLine = false,
                modifier = Modifier.height(150.dp)
            )

        }
        Row(
            modifier = Modifier
                .fillMaxWidth()

        ) {
            Text(text = "Log Out", modifier = Modifier.clickable { OnLogout.invoke() })
        }

    }


}
@Composable
fun ProfileImage(ImageUrl:String?,vm:InstagramViewModel){
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) {uri: Uri? ->
        uri?.let { vm.uploadProfileImage(uri) }
    }

    Box(modifier = Modifier.height(IntrinsicSize.Min)){
        Column(modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { launcher.launch("image/*") },
            horizontalAlignment = Alignment.CenterHorizontally) {
            Card(shape = CircleShape, modifier = Modifier
                .padding(8.dp)
                .size(100.dp)) {
                CommonImage(data = ImageUrl)
                
            }
            Text(text = "Change profile picture")

        }
        val isLoading=vm.inProgress.value
        if(isLoading){
            CommonProgressSpinner()
        }
    }
}