package com.myapp.climatscope.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.myapp.climatscope.ClimatScopeApplication
import com.myapp.climatscope.presentation.navigation.ClimatScopeNavigation
import com.myapp.climatscope.presentation.theme.ClimatScopeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = application as ClimatScopeApplication

        setContent {
            ClimatScopeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ClimatScopeNavigation(
                        navController = rememberNavController(),
                        app = app
                    )
                }
            }
        }
    }
}
