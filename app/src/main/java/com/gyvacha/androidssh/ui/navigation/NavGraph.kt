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
import androidx.navigation.compose.dialog
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.gyvacha.androidssh.R
import com.gyvacha.androidssh.domain.model.navigation.AppNavigation
import com.gyvacha.androidssh.domain.model.navigation.TopAppBarParams
import com.gyvacha.androidssh.ui.components.EditSshKeyDialog
import com.gyvacha.androidssh.ui.components.GenerateSshKeyDialog
import com.gyvacha.androidssh.ui.screens.EditHostScreen
import com.gyvacha.androidssh.ui.screens.EditProxyConfigScreen
import com.gyvacha.androidssh.ui.screens.HostsScreen
import com.gyvacha.androidssh.ui.screens.QrScannerScreen
import com.gyvacha.androidssh.ui.screens.SettingsScreen
import com.gyvacha.androidssh.ui.screens.SingboxScreen
import com.gyvacha.androidssh.ui.screens.TerminalScreen

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
                    navController = navController,
                    topAppBarParams = TopAppBarParams(
                        screenTitle = stringResource(R.string.label_servers),
                        canNavigateBack = navController.previousBackStackEntry != null,
                        navigateUp = navController::navigateUp,
                        actions = {
                            IconButton(onClick = {
                                navController.navigate(AppNavigation.HostsRoute.EditHost()) {
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
            composable<AppNavigation.HostsRoute.EditHost> { backstackEntry ->
                val hostId = backstackEntry.toRoute<AppNavigation.HostsRoute.EditHost>().hostId
                EditHostScreen(
                    navController = navController,
                    topAppBarParams = TopAppBarParams(
                        screenTitle = if (hostId != null) {
                            stringResource(
                                R.string.add_host
                            )
                        } else {
                            stringResource(R.string.edit_host)
                        },
                        canNavigateBack = navController.previousBackStackEntry != null,
                        navigateUp = navController::navigateUp
                    ),
                    hostId = hostId
                )
            }
            composable<AppNavigation.HostsRoute.Terminal> { backstackEntry ->
                val hostId = backstackEntry.toRoute<AppNavigation.HostsRoute.Terminal>().hostId
                TerminalScreen(
                    navController = navController,
                    hostId = hostId,
                    topAppBarParams = TopAppBarParams(
                        screenTitle = stringResource(R.string.terminal),
                        canNavigateBack = navController.previousBackStackEntry != null,
                        navigateUp = navController::navigateUp
                    )
                )
            }
        }

        navigation<AppNavigation.SettingsRoute>(startDestination = AppNavigation.SettingsRoute.Settings) {
            composable<AppNavigation.SettingsRoute.Settings> {
                SettingsScreen(
                    navController = navController,
                    topAppBarParams = TopAppBarParams(
                        screenTitle = stringResource(R.string.label_settings),
                        canNavigateBack = navController.previousBackStackEntry != null,
                        navigateUp = navController::navigateUp
                    )
                )
            }
        }

        navigation<AppNavigation.SingboxRoute>(startDestination = AppNavigation.SingboxRoute.Singbox) {
            composable<AppNavigation.SingboxRoute.Singbox> {
                SingboxScreen(
                    navController = navController,
                    topAppBarParams = TopAppBarParams(
                        screenTitle = stringResource(R.string.label_singbox),
                        canNavigateBack = navController.previousBackStackEntry != null,
                        navigateUp = navController::navigateUp
                    )
                )
            }
            composable<AppNavigation.SingboxRoute.ImportFromQR> {
                QrScannerScreen(
                    navController = navController
                )
            }
            composable<AppNavigation.SingboxRoute.EditProxyConfig> { backstackEntry ->
                val proxyConfigId = backstackEntry.toRoute<AppNavigation.SingboxRoute.EditProxyConfig>().proxyConfigId
                EditProxyConfigScreen(
                    navController = navController,
                    topAppBarParams = TopAppBarParams(
                        screenTitle = if (proxyConfigId != null) {
                            stringResource(
                                R.string.edit_proxy_config
                            )
                        } else {
                            stringResource(R.string.add_proxy_config)
                        },
                        canNavigateBack = navController.previousBackStackEntry != null,
                        navigateUp = navController::navigateUp
                    ),
                    proxyConfigId = proxyConfigId
                )
            }
        }
        dialog<AppNavigation.EditSshKey> { backStackEntry ->
            val sshKeyId = backStackEntry.toRoute<AppNavigation.EditSshKey>().sshKeyId
            EditSshKeyDialog(
                onSave = {
                    navController.navigateUp()
                },
                onDismiss = {
                    navController.navigateUp()
                },
                sshKeyId = sshKeyId
            )
        }
        dialog<AppNavigation.GenerateSshKey> {
            val parentEntry = navController.previousBackStackEntry
            val savedStateHandle = parentEntry?.savedStateHandle
            GenerateSshKeyDialog(
                onSave = { sshKey ->
                    savedStateHandle?.set("generated_ssh_key_id", sshKey.sshKeyId)
                    navController.navigateUp()
                },
                onDismiss = {
                    navController.navigateUp()
                }
            )
        }
    }
}
