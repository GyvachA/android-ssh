package com.gyvacha.androidssh.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gyvacha.androidssh.R
import com.gyvacha.androidssh.domain.model.ProxyConfig
import com.gyvacha.androidssh.domain.model.ProxySpec
import com.gyvacha.androidssh.domain.model.ProxyType
import com.gyvacha.androidssh.domain.model.Status
import com.gyvacha.androidssh.domain.model.navigation.TopAppBarParams
import com.gyvacha.androidssh.ui.components.MenuWithIcon
import com.gyvacha.androidssh.ui.components.RequestNotificationPermission
import com.gyvacha.androidssh.ui.components.RequestVpnPermission
import com.gyvacha.androidssh.ui.components.SingboxConfigCard
import com.gyvacha.androidssh.ui.components.TopAppBarWithBackButton
import com.gyvacha.androidssh.ui.viewmodel.SingboxViewModel
import com.gyvacha.androidssh.utils.ClipboardService
import com.gyvacha.androidssh.utils.LocalMessageNotifier
import com.gyvacha.androidssh.utils.ParseProxyConfig.parseProxyUri
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun SingboxScreen(
    topAppBarParams: TopAppBarParams,
    modifier: Modifier = Modifier,
    viewModel: SingboxViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val configs by viewModel.configs.collectAsStateWithLifecycle()
    val clipboardScope = rememberCoroutineScope()
    val clipboardService = ClipboardService(LocalClipboard.current)
    val snackbarManager = LocalMessageNotifier.current
    val singboxState by viewModel.singboxState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.singboxLogs.collectLatest {
            Log.d("SingboxService LOGS", it)
        }
    }

    val errorParsing = stringResource(R.string.error_get_config_from_copyboard)

    if (uiState.requestPermission) {
        if (!uiState.isNotificationPermissionGranted) {
            RequestNotificationPermission {
                viewModel.updateIsNotificationPermGranted(true)
            }
        }
        if (!uiState.isVPNPermissionGranted) {
            RequestVpnPermission {
                viewModel.updateIsVPNPermGranted(true)
            }
        }
        if (uiState.isVPNPermissionGranted && uiState.isNotificationPermissionGranted) {
            viewModel.startSingbox()
        }
        viewModel.updateRequestPermission(false)
    }

    Scaffold(
        topBar = {
            TopAppBarWithBackButton(
                topAppBarParams.copy(
                    actions = {
                        MenuWithIcon(
                            expanded = uiState.expandedTopAppBarMenu,
                            onDismiss = {
                                viewModel.updateExpandedTopAppBarMenu(false)
                            },
                            onMenuClick = {
                                viewModel.updateExpandedTopAppBarMenu(true)
                            },
                            icon = Icons.Filled.Add
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.import_from_copy_buffer)) },
                                onClick = {
                                    clipboardScope.launch {
                                        val clipboardText = clipboardService.getText()
                                        val proxySpec = clipboardText?.let { parseProxyUri(it) }
                                        if (proxySpec != null) {
                                            val proxyConfig = ProxyConfig(
                                                id = 0,
                                                alias = "Import from config",
                                                type = when (proxySpec) {
                                                    is ProxySpec.Vless -> ProxyType.VLESS
                                                    is ProxySpec.Vmess -> ProxyType.VMESS
                                                    is ProxySpec.Trojan -> ProxyType.TROJAN
                                                    is ProxySpec.Shadowsocks -> ProxyType.SHADOWSOCKS
                                                    is ProxySpec.Socks -> ProxyType.SOCKS
                                                    is ProxySpec.Http -> ProxyType.HTTP
                                                },
                                                config = proxySpec,
                                                isActive = false
                                            )
                                            viewModel.insertConfig(proxyConfig)
                                            viewModel.updateExpandedTopAppBarMenu(false)
                                        } else {
                                            snackbarManager?.showSnackbar(errorParsing)
                                        }
                                    }
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.import_from_qr_code)) },
                                onClick = {
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.add_manually)) },
                                onClick = {
                                }
                            )
                        }
                    }
                )
            )
        },
        bottomBar = {
            Surface(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimensionResource(R.dimen.medium_padding)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                        }
                    ) {
                        Text(stringResource(R.string.ping))
                    }
                    Text(
                        "ping ms",
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = dimensionResource(R.dimen.small_padding))
                    )
                    FloatingActionButton(
                        onClick = {
                            when (singboxState) {
                                Status.Stopped -> viewModel.updateRequestPermission(true)
                                Status.Started -> viewModel.stopSingbox()
                                else -> {}
                            }
                        },
                        shape = CircleShape,
                        containerColor = MaterialTheme.colorScheme.primary,
                    ) {
                        when (singboxState) {
                            Status.Stopped, Status.Stopping -> Icon(
                                Icons.Filled.PlayArrow,
                                contentDescription = stringResource(R.string.start_singbox)
                            )
                            Status.Started, Status.Starting, Status.Restarting -> Icon(
                                Icons.Filled.Stop,
                                contentDescription = stringResource(R.string.stop_singbox)
                            )
                        }
                    }
                }
            }
        },
        modifier = modifier
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding)
        ) {
            items(configs) { config ->
                SingboxConfigCard(
                    config = config,
                    onCardClick = {
                        viewModel.setActiveConfig(config)
                    },
                    onDeleteConfig = {
                        viewModel.deleteConfig(config)
                    }
                )
            }
        }
    }
}
