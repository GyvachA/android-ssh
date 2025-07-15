package com.gyvacha.androidssh.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gyvacha.androidssh.R
import com.gyvacha.androidssh.domain.model.ProxyConfig
import com.gyvacha.androidssh.domain.model.ProxySpec
import com.gyvacha.androidssh.domain.model.ProxyType
import com.gyvacha.androidssh.domain.model.navigation.TopAppBarParams
import com.gyvacha.androidssh.ui.components.MenuWithIcon
import com.gyvacha.androidssh.ui.components.SingboxConfigCard
import com.gyvacha.androidssh.ui.components.TopAppBarWithBackButton
import com.gyvacha.androidssh.ui.viewmodel.SingboxViewModel
import com.gyvacha.androidssh.utils.ClipboardService
import com.gyvacha.androidssh.utils.LocalMessageNotifier
import com.gyvacha.androidssh.utils.ParseProxyConfig.parseProxyUri
import kotlinx.coroutines.launch

@Composable
fun SingboxScreen(
    topAppBarParams: TopAppBarParams,
    modifier: Modifier = Modifier,
    viewModel: SingboxViewModel = hiltViewModel()
) {
    var expandedMenu by rememberSaveable { mutableStateOf(false) }
    val configs by viewModel.configs.collectAsStateWithLifecycle()
    val clipboardScope = rememberCoroutineScope()
    val clipboardService = ClipboardService(LocalClipboard.current)
    val snackbarManager = LocalMessageNotifier.current

    val errorParsing = stringResource(R.string.error_get_config_from_copyboard)

    Scaffold(
        topBar = {
            TopAppBarWithBackButton(
                topAppBarParams.copy(
                    actions = {
                        MenuWithIcon(
                            expanded = expandedMenu,
                            onDismiss = {
                                expandedMenu = false
                            },
                            onMenuClick = {
                                expandedMenu = true
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
                                                type = when(proxySpec) {
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
                                            expandedMenu = false
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
        modifier = modifier
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding)
        ) {
            items(configs) { config ->
                SingboxConfigCard(
                    config = config,
                    onCardClick = {

                    },
                    onDeleteConfig = {
                        viewModel.deleteConfig(config)
                    }
                )
            }
        }
    }
}
