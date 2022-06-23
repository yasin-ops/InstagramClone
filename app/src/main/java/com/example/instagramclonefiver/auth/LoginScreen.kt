package com.example.instagramclonefiver.auth


import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.instagramclonefiver.DestinationScreen
import com.example.instagramclonefiver.InstagramViewModel
import com.example.instagramclonefiver.main.CheckSignedIn
import com.example.instagramclonefiver.main.CommonProgressSpinner
import com.example.instagramclonefiver.main.navigateTO
import com.squareup.okhttp.internal.http.RealResponseBody
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment

@Composable
fun LoginScreen(navController: NavController, vm: InstagramViewModel) {
    CheckSignedIn(vm = vm, navController = navController)
    val focus = LocalFocusManager.current
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .verticalScroll(
                    rememberScrollState()
                ),
            horizontalAlignment = Alignment.CenterHorizontally


        ) {
            val emailState = remember { mutableStateOf(TextFieldValue()) }
            val passState = remember { mutableStateOf(TextFieldValue()) }
            Image(
                painter = painterResource(id = com.example.instagramclonefiver.R.drawable.ig_logo),
                contentDescription = null, modifier = Modifier
                    .width(250.dp)
                    .padding(8.dp)
                    .padding(top = 16.dp)

            )
            Text(
                text = "Login",
                modifier = Modifier.padding(8.dp),
                fontSize = 13.sp,
                fontFamily = FontFamily.Cursive
            )


            OutlinedTextField(value = emailState.value,
                onValueChange = { emailState.value = it },
                modifier = Modifier.padding(8.dp),
                label = { Text(text = "Email") }
            )

            OutlinedTextField(
                value = passState.value,
                onValueChange = { passState.value = it },
                modifier = Modifier.padding(8.dp),
                label = { Text(text = "Passwords") },
                visualTransformation = PasswordVisualTransformation()

            )
            Button(
                onClick = {

                    focus.clearFocus(force = true)
                    vm.onLogin(emailState.value.text,passState.value.text)

                },
                modifier = Modifier.padding(8.dp),

            )
            {
                Text(text = "Login")


            }
            Text(text = "Do not have  account go to Sign UP Screen !",
                color = Color.Blue,


                modifier = Modifier
                    .padding(8.dp)

                    .clickable {
                        navigateTO(navController, DestinationScreen.Signup)
                    }

            )


        }


        val isLoading = vm.inProgress.value
        if (isLoading) {
            CommonProgressSpinner()
        }

    }


}



