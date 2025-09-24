package com.gyvacha.androidssh.ui.screens

import android.content.Intent
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.gyvacha.androidssh.domain.model.navigation.AppNavigation
import com.gyvacha.androidssh.service.SingboxService
import com.gyvacha.androidssh.ui.navigation.BottomNavGraph
import com.gyvacha.androidssh.ui.navigation.BottomNavigationBar
import com.gyvacha.androidssh.utils.LocalMessageNotifier
import com.gyvacha.androidssh.utils.SnackbarNotifier
import com.gyvacha.androidssh.utils.applyNavBarPadding
import com.gyvacha.androidssh.utils.applySystemBarsPadding

@Composable
fun MainScreen(intent: Intent, modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val bottomBarShowScreenList = listOf(
        AppNavigation.SettingsRoute.Settings,
        AppNavigation.HostsRoute.Hosts,
        AppNavigation.SingboxRoute.Singbox
    )
    val shouldShowBottomBar = bottomBarShowScreenList.any {
        currentDestination?.hasRoute(it::class) ?: false
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val snackbarNotifier = remember { SnackbarNotifier(snackbarHostState, coroutineScope) }

    LaunchedEffect(intent) {
        if (intent.getBooleanExtra(SingboxService.NAVIGATE_TO_SINGBOX, false)) {
            navController.navigate(AppNavigation.SingboxRoute.Singbox) {
                popUpTo(navController.graph.startDestinationId) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        }
    }
    CompositionLocalProvider(LocalMessageNotifier provides snackbarNotifier) {
        Scaffold(
            modifier = modifier,
            contentWindowInsets = WindowInsets.safeDrawing,
            bottomBar = {
                if (shouldShowBottomBar) {
                    BottomNavigationBar(
                        navController = navController,
                        currentDestination = currentDestination,
                        modifier = Modifier.applyNavBarPadding()
                    )
                }
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
        ) { innerPadding ->
            BottomNavGraph(
                navController = navController,
                modifier = Modifier.padding(innerPadding)
                    .applySystemBarsPadding()
            )
        }
    }
}
