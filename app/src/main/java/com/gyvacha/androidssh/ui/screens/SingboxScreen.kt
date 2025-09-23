package com.gyvacha.androidssh.ui.screens

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
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.gyvacha.androidssh.BuildConfig
import com.gyvacha.androidssh.R
import com.gyvacha.androidssh.domain.model.PingResult
import com.gyvacha.androidssh.domain.model.ProxyConfig
import com.gyvacha.androidssh.domain.model.ProxySpec
import com.gyvacha.androidssh.domain.model.ProxyType
import com.gyvacha.androidssh.domain.model.Status
import com.gyvacha.androidssh.domain.model.navigation.AppNavigation
import com.gyvacha.androidssh.domain.model.navigation.TopAppBarParams
import com.gyvacha.androidssh.ui.components.FeedAdList
import com.gyvacha.androidssh.ui.components.MenuWithIcon
import com.gyvacha.androidssh.ui.components.RequestNotificationPermission
import com.gyvacha.androidssh.ui.components.RequestVpnPermission
import com.gyvacha.androidssh.ui.components.SingboxConfigCard
import com.gyvacha.androidssh.ui.components.TopAppBarWithBackButton
import com.gyvacha.androidssh.ui.utils.SingboxViewEvent
import com.gyvacha.androidssh.ui.viewmodel.FeedAdsViewModel
import com.gyvacha.androidssh.ui.viewmodel.SingboxViewModel
import com.gyvacha.androidssh.utils.ClipboardService
import com.gyvacha.androidssh.utils.LocalMessageNotifier
import com.gyvacha.androidssh.utils.ParseProxyConfig.parseProxyUri
import kotlinx.coroutines.launch

@Composable
fun SingboxScreen(
    topAppBarParams: TopAppBarParams,
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: SingboxViewModel = hiltViewModel(),
    feedAdsViewModel: FeedAdsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val configs by viewModel.configs.collectAsStateWithLifecycle()
    val clipboardScope = rememberCoroutineScope()
    val messageNotifier = LocalMessageNotifier.current
    val clipboardService = ClipboardService(LocalClipboard.current)
    val snackbarManager = LocalMessageNotifier.current
    val singboxState by viewModel.singboxState.collectAsStateWithLifecycle()
    val feedAd by feedAdsViewModel.feedAd.collectAsStateWithLifecycle()

    val serviceErrorNoActiveConfig = stringResource(R.string.no_active_config)
    val serviceStartError = stringResource(R.string.service_start_error)
    val serviceStarted = stringResource(R.string.singbox_service_started)

    LaunchedEffect(Unit) {
        feedAdsViewModel.loadFeed(BuildConfig.YANDEX_AD_FEED_ID_SECOND)
        viewModel.eventFlow.collect { event ->
            when (event) {
                SingboxViewEvent.ServiceErrorNoActiveConfig -> messageNotifier?.showSnackbar(
                    serviceErrorNoActiveConfig
                )

                SingboxViewEvent.ServiceStared -> messageNotifier?.showSnackbar(
                    serviceStarted
                )

                SingboxViewEvent.ServiceStartError -> messageNotifier?.showSnackbar(
                    serviceStartError
                )
            }
        }
    }

    LaunchedEffect(uiState.isVPNPermissionGranted, uiState.isNotificationPermissionGranted) {
        if (uiState.isVPNPermissionGranted && uiState.isNotificationPermissionGranted) {
            viewModel.startSingbox()
            viewModel.updateIsVPNPermGranted(false)
            viewModel.updateIsNotificationPermGranted(false)
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
                                        val proxy = clipboardText?.let { parseProxyUri(it) }
                                        if (proxy != null) {
                                            val alias = proxy.first
                                            val proxySpec = proxy.second
                                            val proxyConfig = ProxyConfig(
                                                id = 0,
                                                alias = alias,
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
                                        } else {
                                            snackbarManager?.showSnackbar(errorParsing)
                                        }
                                        viewModel.updateExpandedTopAppBarMenu(false)
                                    }
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.import_from_qr_code)) },
                                onClick = {
                                    viewModel.updateExpandedTopAppBarMenu(false)
                                    navController.navigate(AppNavigation.XrayRoute.ImportFromQR)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.add_manually)) },
                                onClick = {
                                    viewModel.updateExpandedTopAppBarMenu(false)
                                    navController.navigate(AppNavigation.XrayRoute.EditProxyConfig())
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
                            viewModel.requestPing()
                        }
                    ) {
                        Text(stringResource(R.string.ping))
                    }
                    Text(
                        text = when (uiState.pingResult) {
                            is PingResult.Idle -> ""
                            is PingResult.Success -> "${(uiState.pingResult as PingResult.Success).ms} ms"
                            is PingResult.Failure -> "Error: ${(uiState.pingResult as PingResult.Failure).error}"
                        },
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
                            Status.Stopped -> Icon(
                                Icons.Filled.PlayArrow,
                                contentDescription = stringResource(R.string.start_singbox)
                            )
                            Status.Started -> Icon(
                                Icons.Filled.Stop,
                                contentDescription = stringResource(R.string.stop_singbox)
                            )
                            Status.Stopping, Status.Restarting, Status.Starting -> {
                                CircularProgressIndicator()
                            }
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
            if (configs.isEmpty()) {
                item {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(R.string.empty_yet),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
            items(configs) { config ->
                SingboxConfigCard(
                    config = config,
                    onCardClick = {
                        viewModel.setActiveConfig(config)
                    },
                    onDeleteConfig = {
                        viewModel.deleteConfig(config)
                    },
                    onUpdateConfig = {
                        navController.navigate(AppNavigation.XrayRoute.EditProxyConfig(it))
                    }
                )
            }
            item {
                FeedAdList(feedAd = feedAd)
            }
        }
    }
}
