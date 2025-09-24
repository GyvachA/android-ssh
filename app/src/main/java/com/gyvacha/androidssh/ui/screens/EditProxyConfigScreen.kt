package com.gyvacha.androidssh.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.gyvacha.androidssh.R
import com.gyvacha.androidssh.domain.model.ProxySpec
import com.gyvacha.androidssh.domain.model.ProxyType
import com.gyvacha.androidssh.domain.model.navigation.TopAppBarParams
import com.gyvacha.androidssh.ui.components.BottomFabSaveActions
import com.gyvacha.androidssh.ui.components.TextFieldCharacterCount
import com.gyvacha.androidssh.ui.components.TextFieldErrors
import com.gyvacha.androidssh.ui.components.TopAppBarWithBackButton
import com.gyvacha.androidssh.ui.components.getTextFieldErrorMessage
import com.gyvacha.androidssh.ui.screens.form.HttpConfigForm
import com.gyvacha.androidssh.ui.screens.form.ShadowsocksConfigForm
import com.gyvacha.androidssh.ui.screens.form.SocksConfigForm
import com.gyvacha.androidssh.ui.screens.form.TrojanConfigForm
import com.gyvacha.androidssh.ui.screens.form.VlessConfigForm
import com.gyvacha.androidssh.ui.screens.form.VmessConfigForm
import com.gyvacha.androidssh.ui.viewmodel.EditProxyConfigViewModel

@Composable
fun EditProxyConfigScreen(
    navController: NavController,
    topAppBarParams: TopAppBarParams,
    modifier: Modifier = Modifier,
    viewModel: EditProxyConfigViewModel = hiltViewModel(),
    proxyConfigId: Long? = null,
    maxTextLength: Int = 60
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    val chipsScrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        if (proxyConfigId != null) {
            viewModel.getProxyConfig(proxyConfigId)
        }
    }

    Scaffold(
        topBar = { TopAppBarWithBackButton(topAppBarParams) },
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                BottomFabSaveActions(
                    isSaveButtonActive = uiState.isFormValid,
                    onSave = {
                        if (proxyConfigId == null) {
                            viewModel.insertProxyConfig()
                            navController.navigateUp()
                        } else {
                            viewModel.updateProxyConfig()
                            navController.navigateUp()
                        }
                    },
                    onCancel = {
                        navController.navigateUp()
                    },
                    modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.medium_padding))
                        .padding(bottom = dimensionResource(R.dimen.medium_padding))
                )
            }
        },
        modifier = modifier
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(dimensionResource(R.dimen.medium_padding)),
        ) {
            Text(stringResource(R.string.proxy_type))
            Spacer(Modifier.padding(dimensionResource(R.dimen.small_padding)))
            Row(
                modifier = Modifier.horizontalScroll(chipsScrollState)
            ) {
                ProxyType.entries.forEach { type ->
                    FilterChip(
                        label = { Text(type.name) },
                        onClick = { viewModel.updateProxyType(type) },
                        selected = uiState.proxyConfig.type == type,
                        modifier = Modifier.padding(end = dimensionResource(R.dimen.small_padding)),
                        shape = RoundedCornerShape(dimensionResource(R.dimen.large_round_corner))
                    )
                }
            }
            Spacer(Modifier.padding(dimensionResource(R.dimen.small_padding)))
            TextFieldCharacterCount(
                value = uiState.proxyConfig.alias,
                onValueChange = {
                    viewModel.updateAlias(it)
                },
                isError = uiState.proxyConfig.alias.isBlank(),
                errorMessage = getTextFieldErrorMessage(TextFieldErrors.STRING_BLANK_ERROR),
                label = { Text(text = stringResource(R.string.alias) + "*") },
                maxLength = maxTextLength
            )
            Spacer(Modifier.padding(dimensionResource(R.dimen.small_padding)))
            TextFieldCharacterCount(
                value = uiState.proxyConfig.config.server,
                onValueChange = {
                    val newAddress = it.trim()
                    viewModel.updateServer(newAddress)
                },
                isError = uiState.proxyConfig.config.server.isBlank(),
                errorMessage = getTextFieldErrorMessage(TextFieldErrors.STRING_BLANK_ERROR),
                label = { Text(text = stringResource(R.string.address) + "*") },
                maxLength = maxTextLength
            )
            Spacer(Modifier.padding(dimensionResource(R.dimen.small_padding)))
            TextFieldCharacterCount(
                value = uiState.proxyConfig.config.port.toString(),
                onValueChange = {
                    var newPort = it.filter { char -> char.isDigit() }
                    if (newPort.isBlank()) newPort = "0"
                    viewModel.updatePort(newPort)
                },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                label = { Text(text = stringResource(R.string.port) + "*") },
                maxLength = 5,
                isError = false
            )
            Spacer(Modifier.padding(dimensionResource(R.dimen.small_padding)))
            when (val config = uiState.proxyConfig.config) {
                is ProxySpec.Vless -> VlessConfigForm(
                    config = config,
                    onUpdate = { viewModel.updateConfig(it) }
                )

                is ProxySpec.Vmess -> VmessConfigForm(
                    config = config,
                    onUpdate = { viewModel.updateConfig(it) }
                )

                is ProxySpec.Trojan -> TrojanConfigForm(
                    config = config,
                    onUpdate = { viewModel.updateConfig(it) }
                )

                is ProxySpec.Shadowsocks -> ShadowsocksConfigForm(
                    config = config,
                    onUpdate = { viewModel.updateConfig(it) }
                )

                is ProxySpec.Socks -> SocksConfigForm(
                    config = config,
                    onUpdate = { viewModel.updateConfig(it) }
                )

                is ProxySpec.Http -> HttpConfigForm(
                    config = config,
                    onUpdate = { viewModel.updateConfig(it) }
                )
            }
        }
    }
}
