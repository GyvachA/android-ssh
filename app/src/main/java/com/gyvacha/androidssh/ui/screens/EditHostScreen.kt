package com.gyvacha.androidssh.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.gyvacha.androidssh.R
import com.gyvacha.androidssh.domain.model.SshAuthType
import com.gyvacha.androidssh.domain.model.SshKey
import com.gyvacha.androidssh.domain.model.navigation.AppNavigation
import com.gyvacha.androidssh.domain.model.navigation.TopAppBarParams
import com.gyvacha.androidssh.ui.components.BottomFabSaveActions
import com.gyvacha.androidssh.ui.components.SecureTextField
import com.gyvacha.androidssh.ui.components.SshKeyCard
import com.gyvacha.androidssh.ui.components.SshKeysBottomSheet
import com.gyvacha.androidssh.ui.components.TextFieldCharacterCount
import com.gyvacha.androidssh.ui.components.TextFieldErrors
import com.gyvacha.androidssh.ui.components.TopAppBarWithBackButton
import com.gyvacha.androidssh.ui.components.getTextFieldErrorMessage
import com.gyvacha.androidssh.ui.utils.EditHostViewEvent
import com.gyvacha.androidssh.ui.viewmodel.EditHostViewModel
import com.gyvacha.androidssh.utils.LocalMessageNotifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditHostScreen(
    navController: NavController,
    topAppBarParams: TopAppBarParams,
    modifier: Modifier = Modifier,
    viewModel: EditHostViewModel = hiltViewModel(),
    hostId: Int? = null,
    maxTextLength: Int = 60
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    val messageNotifier = LocalMessageNotifier.current
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val savedStateHandle = navBackStackEntry?.savedStateHandle
    val isDialogOpen =
        navBackStackEntry?.destination?.hasRoute<AppNavigation.GenerateSshKey>() == true

    val messageHostCreated = stringResource(R.string.host_added_succesfully)
    val messageSshKeyCreated = stringResource(R.string.ssh_key_created)
    val messageHostCreateError = stringResource(R.string.host_create_failure)
    val messageHostUpdated = stringResource(R.string.host_updated)
    val messageSshKeyCreateError = stringResource(R.string.ssh_key_create_failure)
    LaunchedEffect(savedStateHandle) {
        savedStateHandle?.getStateFlow<Int?>("generated_ssh_key_id", null)?.collect { key ->
            key?.let {
                viewModel.updateSshKeyWithId(it)
                viewModel.updateShowBottomSheet(false)
                savedStateHandle.remove<Int>("generated_ssh_key_id")
            }
        }
    }
    LaunchedEffect(Unit) {
        if (hostId != null) {
            viewModel.getHostWithSshKey(hostId)
        }

        viewModel.eventFlow.collect { event ->
            when (event) {
                is EditHostViewEvent.DatabaseExceptionCaught -> messageNotifier?.showSnackbar(
                    messageHostCreateError
                )

                EditHostViewEvent.HostInserted -> messageNotifier?.showSnackbar(
                    messageHostCreated
                )

                EditHostViewEvent.NavigateUp -> navController.navigateUp()
                EditHostViewEvent.SshKeyCreateFailure -> messageNotifier?.showSnackbar(
                    messageSshKeyCreateError
                )

                EditHostViewEvent.SshKeyCreated -> messageNotifier?.showSnackbar(
                    messageSshKeyCreated
                )

                EditHostViewEvent.HostUpdated -> messageNotifier?.showSnackbar(
                    messageHostUpdated
                )
            }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = { TopAppBarWithBackButton(topAppBarParams) },
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                BottomFabSaveActions(
                    isSaveButtonActive = uiState.isFormValid,
                    onSave = {
                        if (hostId == null) {
                            viewModel.insertHost()
                        } else {
                            viewModel.updateHost()
                        }
                    },
                    onCancel = {
                        navController.navigateUp()
                    }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(dimensionResource(R.dimen.medium_padding))
                .verticalScroll(scrollState),
        ) {
            TextFieldCharacterCount(
                value = uiState.hostWithSshKey.host.alias,
                onValueChange = {
                    viewModel.updateAlias(it)
                },
                isError = uiState.hostWithSshKey.host.alias.isBlank(),
                errorMessage = getTextFieldErrorMessage(TextFieldErrors.STRING_BLANK_ERROR),
                label = { Text(text = stringResource(R.string.alias) + "*") },
                maxLength = maxTextLength
            )
            Spacer(Modifier.padding(dimensionResource(R.dimen.small_padding)))
            TextFieldCharacterCount(
                value = uiState.hostWithSshKey.host.hostNameOrIp,
                onValueChange = {
                    val newHostNameOrIp = it.trim()
                    viewModel.updateHostNameOrIp(newHostNameOrIp)
                },
                isError = uiState.hostWithSshKey.host.hostNameOrIp.isBlank(),
                errorMessage = getTextFieldErrorMessage(TextFieldErrors.STRING_BLANK_ERROR),
                label = { Text(text = stringResource(R.string.address) + "*") },
                maxLength = maxTextLength
            )
            Spacer(Modifier.padding(dimensionResource(R.dimen.small_padding)))
            TextFieldCharacterCount(
                value = uiState.hostWithSshKey.host.port.toString(),
                onValueChange = {
                    var newPort = it.filter { char -> char.isDigit() }
                    if (newPort.isBlank()) newPort = "0"
                    viewModel.updatePort(newPort)
                },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                label = { Text(text = stringResource(R.string.port) + "*") },
                maxLength = 5,
                isError = false,
                errorMessage = getTextFieldErrorMessage(TextFieldErrors.STRING_BLANK_ERROR)
            )
            Spacer(Modifier.padding(dimensionResource(R.dimen.small_padding)))
            TextFieldCharacterCount(
                value = uiState.hostWithSshKey.host.userName,
                onValueChange = {
                    viewModel.updateUserName(it)
                },
                label = { Text(text = stringResource(R.string.user_name) + "*") },
                maxLength = maxTextLength,
                isError = uiState.hostWithSshKey.host.userName.isBlank(),
                errorMessage = getTextFieldErrorMessage(TextFieldErrors.STRING_BLANK_ERROR)
            )
            Spacer(Modifier.padding(dimensionResource(R.dimen.small_padding)))
            Text(stringResource(R.string.auth_method))
            Spacer(Modifier.padding(dimensionResource(R.dimen.small_padding)))
            Row {
                FilterChip(
                    label = { Text(stringResource(R.string.password)) },
                    onClick = {
                        viewModel.updateSshAuthType(SshAuthType.PASSWORD)
                    },
                    selected = uiState.hostWithSshKey.host.authType == SshAuthType.PASSWORD,
                    modifier = Modifier
                        .padding(end = dimensionResource(R.dimen.small_padding)),
                    shape = RoundedCornerShape(dimensionResource(R.dimen.large_round_corner))
                )
                FilterChip(
                    label = { Text(stringResource(R.string.ssh_key)) },
                    onClick = {
                        viewModel.updateSshAuthType(SshAuthType.SSH_KEY)
                    },
                    selected = uiState.hostWithSshKey.host.authType == SshAuthType.SSH_KEY,
                    shape = RoundedCornerShape(dimensionResource(R.dimen.large_round_corner))
                )
            }
            Spacer(Modifier.padding(dimensionResource(R.dimen.small_padding)))
            when (uiState.hostWithSshKey.host.authType) {
                SshAuthType.PASSWORD -> {
                    SecureTextField(
                        value = uiState.hostWithSshKey.host.password ?: "",
                        onValueChange = viewModel::updatePassword,
                        label = stringResource(R.string.password),
                        onVisibilityClick = { viewModel.updatePasswordVisibility(!uiState.isPasswordVisible) },
                        isPasswordVisible = uiState.isPasswordVisible
                    )
                }

                SshAuthType.SSH_KEY -> {
                    if (uiState.hostWithSshKey.sshKey == null) {
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                viewModel.updateShowBottomSheet(true)
                            }
                        ) {
                            Text(stringResource(R.string.choose_ssh_key))
                            Icon(
                                imageVector = Icons.Filled.ArrowDropDown,
                                contentDescription = stringResource(R.string.choose_ssh_key)
                            )
                        }
                    } else {
                        SshKeyCard(
                            onClick = { viewModel.updateShowBottomSheet(true) },
                            sshKey = uiState.hostWithSshKey.sshKey ?: SshKey(
                                alias = "Error",
                                publicKey = "",
                                privateKey = ""
                            ),
                            actionButtonImage = Icons.Filled.KeyboardArrowDown,
                            actionButtonDesc = stringResource(R.string.update_ssh_key),
                            modifier = Modifier.fillMaxWidth(),
                            isShowMenu = true
                        )
                    }
                }
            }
        }

        if (uiState.isShowBottomSheet && !isDialogOpen) {
            SshKeysBottomSheet(
                onDismissRequest = { viewModel.updateShowBottomSheet(false) },
                generateSshKeyClick = {
                    navController.navigate(AppNavigation.GenerateSshKey)
                }
            ) { sshKey ->
                SshKeyCard(
                    sshKey = sshKey,
                    onClick = {
                        viewModel.updateShowBottomSheet(false)
                        viewModel.updateSshKey(sshKey)
                    },
                    actionButtonImage = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    actionButtonDesc = stringResource(R.string.choose_ssh_key)
                )
            }
        }
    }
}

@Composable
@Preview
private fun AddHostPreview() {
    EditHostScreen(rememberNavController(), TopAppBarParams.PREVIEW)
}
