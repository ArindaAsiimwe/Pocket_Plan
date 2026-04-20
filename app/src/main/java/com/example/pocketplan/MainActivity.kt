package com.example.pocketplan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.pocketplan.ui.components.PocketPlanBottomBar
import com.example.pocketplan.ui.navigation.AppNavGraph
import com.example.pocketplan.ui.navigation.Screen
import com.example.pocketplan.ui.theme.PocketPlanTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PocketPlanTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                
                // Observe the lifecycle state of the current backstack entry
                val lifecycleState by (navBackStackEntry?.lifecycle?.currentStateFlow 
                    ?: MutableStateFlow(Lifecycle.State.INITIALIZED))
                    .collectAsStateWithLifecycle()

                val currentRoute = navBackStackEntry?.destination?.route

                // Show bottom bar if we're on a main screen AND the transition has started (STARTED)
                val showBottomBar = currentRoute != null && 
                                   currentRoute != Screen.Login.route && 
                                   currentRoute != Screen.Register.route &&
                                   lifecycleState.isAtLeast(Lifecycle.State.STARTED)

                Scaffold(
                    bottomBar = {
                        AnimatedVisibility(
                            visible = showBottomBar,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            PocketPlanBottomBar(navController = navController)
                        }
                    }
                ) { innerPadding ->
                    AppNavGraph(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
