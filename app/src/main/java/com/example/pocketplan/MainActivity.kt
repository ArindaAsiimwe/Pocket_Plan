package com.example.pocketplan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.example.pocketplan.ui.auth.LoginScreen
import com.example.pocketplan.ui.auth.RegisterScreen
import com.example.pocketplan.ui.theme.PocketPlanTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PocketPlanTheme {
                var currentScreen by remember { mutableStateOf("login") }

                when (currentScreen) {
                    "login" -> LoginScreen(
                        onLoginClick = { /* Navigate to Home */ },
                        onRegisterClick = { currentScreen = "register" }
                    )
                    "register" -> RegisterScreen(
                        onRegisterClick = { /* Handle registration */ },
                        onLoginClick = { currentScreen = "login" }
                    )
                }
            }
        }
    }
}
