package com.gyvacha.androidssh.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.gyvacha.androidssh.R
import com.gyvacha.androidssh.domain.model.navigation.AppNavigation
import com.gyvacha.androidssh.domain.model.navigation.TopAppBarParams
import com.gyvacha.androidssh.ui.screens.AddHostScreen
import com.gyvacha.androidssh.ui.screens.HostsScreen
import com.gyvacha.androidssh.ui.screens.SettingsScreen
import com.gyvacha.androidssh.ui.screens.XrayScreen

@Composable
fun BottomNavGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = AppNavigation.HostsRoute,
        modifier = modifier
    ) {
        navigation<AppNavigation.HostsRoute>(startDestination = AppNavigation.HostsRoute.Hosts) {
            composable<AppNavigation.HostsRoute.Hosts> {
                HostsScreen(
                    topAppBarParams = TopAppBarParams(
                        screenTitle = stringResource(R.string.label_servers),
                        canNavigateBack = navController.previousBackStackEntry != null,
                        navigateUp = navController::navigateUp,
                        actions = {
                            IconButton(onClick = {
                                navController.navigate(AppNavigation.HostsRoute.AddHost) {
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.Add,
                                    contentDescription = stringResource(R.string.add)
                                )
                            }
                        }
                    )
                )
            }
            composable<AppNavigation.HostsRoute.AddHost> {
                AddHostScreen(
                    topAppBarParams = TopAppBarParams(
                        screenTitle = stringResource(R.string.add_host),
                        canNavigateBack = navController.previousBackStackEntry != null,
                        navigateUp = navController::navigateUp
                    )
                )
            }
        }

        navigation<AppNavigation.SettingsRoute>(startDestination = AppNavigation.SettingsRoute.Settings) {
            composable<AppNavigation.SettingsRoute.Settings> {
                SettingsScreen(
                    topAppBarParams = TopAppBarParams(
                        screenTitle = stringResource(R.string.label_settings),
                        canNavigateBack = navController.previousBackStackEntry != null,
                        navigateUp = navController::navigateUp
                    )
                )
//            ComingSoonScreen()
            }
        }

        navigation<AppNavigation.XrayRoute>(startDestination = AppNavigation.XrayRoute.Xray) {
            composable<AppNavigation.XrayRoute.Xray> {
                XrayScreen(
                    topAppBarParams = TopAppBarParams(
                        screenTitle = stringResource(R.string.label_xray),
                        canNavigateBack = navController.previousBackStackEntry != null,
                        navigateUp = navController::navigateUp
                    )
                )
//            ComingSoonScreen()
            }
        }

    }
}