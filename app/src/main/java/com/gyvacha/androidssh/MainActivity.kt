package com.gyvacha.androidssh

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.gyvacha.androidssh.ui.screens.MainScreen
import com.gyvacha.androidssh.ui.theme.AndroidSshTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splash = installSplashScreen()
        var keepSplash = true
        splash.setKeepOnScreenCondition { keepSplash }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidSshTheme {
                LaunchedEffect(Unit) {
                    delay(1000)
                    keepSplash = false
                }
                AndroidSshApp()
            }
        }
    }
}

@Composable
fun AndroidSshApp() {
    MainScreen()
}
