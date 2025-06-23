package com.gyvacha.androidssh

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import com.gyvacha.androidssh.ui.screens.MainScreen
import com.gyvacha.androidssh.ui.theme.AndroidSshTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidSshTheme {
                AndroidSshApp()
            }
        }
    }
}

@Composable
fun AndroidSshApp() {
    MainScreen()
}
