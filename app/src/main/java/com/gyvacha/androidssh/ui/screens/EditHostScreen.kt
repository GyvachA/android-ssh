package com.gyvacha.androidssh.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.gyvacha.androidssh.R
import com.gyvacha.androidssh.domain.model.SshAuthType
import com.gyvacha.androidssh.domain.model.SshKey
import com.gyvacha.androidssh.domain.model.navigation.TopAppBarParams
import com.gyvacha.androidssh.ui.components.BaseCard
import com.gyvacha.androidssh.ui.components.BottomFabSaveActions
import com.gyvacha.androidssh.ui.components.GenerateSshKeyDialog
import com.gyvacha.androidssh.ui.components.SecureTextField
import com.gyvacha.androidssh.ui.components.SshKeyCard
import com.gyvacha.androidssh.ui.components.TextFieldCharacterCount
import com.gyvacha.androidssh.ui.components.TextFieldErrors
import com.gyvacha.androidssh.ui.components.TopAppBarWithBackButton
import com.gyvacha.androidssh.ui.components.getTextFieldErrorMessage
import com.gyvacha.androidssh.ui.utils.ViewEvent
import com.gyvacha.androidssh.ui.viewmodel.EditHostViewModel
import com.gyvacha.androidssh.utils.LocalMessageNotifier
import com.gyvacha.androidssh.utils.SshKeyGenerator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditHostScreen(
    navController: NavController,
    topAppBarParams: TopAppBarParams,
    modifier: Modifier = Modifier,
    viewModel: EditHostViewModel = hiltViewModel(),
    hostId: Int? = null,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    val messageNotifier = LocalMessageNotifier.current
    val sshKeySheetState = rememberModalBottomSheetState()

    val messageHostCreated = stringResource(R.string.host_added_succesfully)
    val messageSshKeyCreated = stringResource(R.string.ssh_key_created)
    val messageHostCreateError = stringResource(R.string.host_create_failure)
    val messageSshKeyCreateError = stringResource(R.string.ssh_key_create_failure)
    LaunchedEffect(Unit) {
        if (hostId != null) {
            viewModel.getHostWithSshKey(hostId)
        }

        viewModel.eventFlow.collect { event ->
            when (event) {
                is ViewEvent.DatabaseExceptionCaught -> messageNotifier?.showSnackbar(
                    messageHostCreateError
                )

                ViewEvent.HostInserted -> messageNotifier?.showSnackbar(messageHostCreated)
                ViewEvent.NavigateUp -> navController.navigateUp()
                ViewEvent.SshKeyCreateFailure -> messageNotifier?.showSnackbar(
                    messageSshKeyCreateError
                )

                ViewEvent.SshKeyCreated -> messageNotifier?.showSnackbar(messageSshKeyCreated)
            }
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
                        viewModel.insertHost()
                    },
                    onCancel = {
                        navController.navigateUp()
                    }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = modifier
                .padding(padding)
                .padding(dimensionResource(R.dimen.medium_padding))
                .verticalScroll(scrollState),
        ) {
            val maxTextLength = 60
            TextFieldCharacterCount(
                value = uiState.hostWithSshKey.host.alias,
                onValueChange = {
                    var isError: TextFieldErrors? = null
                    if (it.isBlank()) isError = TextFieldErrors.STRING_BLANK_ERROR
                    viewModel.updateAlias(it, isError)
                },
                isError = uiState.isAliasError != null,
                errorMessage = getTextFieldErrorMessage(uiState.isAliasError),
                label = { Text(text = stringResource(R.string.alias)) },
                maxLength = maxTextLength
            )
            Spacer(Modifier.padding(dimensionResource(R.dimen.small_padding)))
            TextFieldCharacterCount(
                value = uiState.hostWithSshKey.host.hostNameOrIp,
                onValueChange = {
                    val newHostNameOrIp = it.trim()
                    var isError: TextFieldErrors? = null
                    if (newHostNameOrIp.length > maxTextLength) isError =
                        TextFieldErrors.STRING_LENGTH_ERROR
                    if (newHostNameOrIp.isBlank()) isError = TextFieldErrors.STRING_BLANK_ERROR
                    viewModel.updateHostNameOrIp(newHostNameOrIp, isError)
                },
                isError = uiState.isHostNameOrIpError != null,
                errorMessage = getTextFieldErrorMessage(uiState.isHostNameOrIpError),
                label = { Text(text = stringResource(R.string.address)) },
                maxLength = maxTextLength
            )
            Spacer(Modifier.padding(dimensionResource(R.dimen.small_padding)))
            TextFieldCharacterCount(
                value = uiState.hostWithSshKey.host.port.toString(),
                onValueChange = {
                    var newPort = it.filter { char -> char.isDigit() }
                    if (newPort.isBlank()) newPort = "0"
                    var isError: TextFieldErrors? = null
                    if (newPort.length > 5) isError = TextFieldErrors.STRING_LENGTH_ERROR
                    viewModel.updatePort(
                        newPort,
                        isError
                    )
                },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                label = { Text(text = stringResource(R.string.port)) },
                maxLength = 5,
                isError = uiState.isPortError != null,
                errorMessage = getTextFieldErrorMessage(uiState.isPortError)
            )
            Spacer(Modifier.padding(dimensionResource(R.dimen.small_padding)))
            TextFieldCharacterCount(
                value = uiState.hostWithSshKey.host.userName,
                onValueChange = {
                    val newUsername = it
                    var isError: TextFieldErrors? = null
                    if (newUsername.length > maxTextLength) isError =
                        TextFieldErrors.STRING_LENGTH_ERROR
                    if (newUsername.isBlank()) isError = TextFieldErrors.STRING_BLANK_ERROR
                    viewModel.updateUserName(
                        newUsername,
                        isError
                    )
                },
                label = { Text(text = stringResource(R.string.user_name)) },
                maxLength = maxTextLength,
                isError = uiState.isUserNameError != null,
                errorMessage = getTextFieldErrorMessage(uiState.isUserNameError)
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
                    shape = RoundedCornerShape(50)
                )
                FilterChip(
                    label = { Text(stringResource(R.string.ssh_key)) },
                    onClick = {
                        viewModel.updateSshAuthType(SshAuthType.SSH_KEY)
                    },
                    selected = uiState.hostWithSshKey.host.authType == SshAuthType.SSH_KEY,
                    shape = RoundedCornerShape(50)
                )
            }
            Spacer(Modifier.padding(dimensionResource(R.dimen.small_padding)))
            when(uiState.hostWithSshKey.host.authType) {
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
                            sshKey = uiState.hostWithSshKey.sshKey ?: SshKey(alias = "Error", publicKey = ""),
                            actionButtonImage = Icons.Filled.Update,
                            actionButtonDesc = stringResource(R.string.update_ssh_key),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        if (uiState.isShowBottomSheet && !uiState.isShowGenerateSshKeyDialog) {
            val sshKeys by viewModel.sshKeys.collectAsStateWithLifecycle()

            ModalBottomSheet(
                sheetState = sshKeySheetState,
                onDismissRequest = { viewModel.updateShowBottomSheet(false) },
                modifier = modifier.padding(
                    top = padding.calculateTopPadding(),
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(dimensionResource(R.dimen.medium_padding))
                ) {
                    BaseCard(
                        onClick = { viewModel.updateShowGenerateSshKeyDialog(true) }
                    ) {
                        Row(
                            modifier = Modifier.padding(dimensionResource(R.dimen.medium_padding)),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.create_ssh_key),
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = {
                                    viewModel.updateShowGenerateSshKeyDialog(true)
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Add,
                                    contentDescription = stringResource(R.string.create_ssh_key)
                                )
                            }
                        }
                    }
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f),
                    ) {
                        items(sshKeys) { sshKey ->
                            SshKeyCard(
                                sshKey = sshKey,
                                onClick = {
                                    viewModel.updateShowBottomSheet(false)
                                    viewModel.updateSshKey(sshKey)
                                },
                                actionButtonImage = Icons.AutoMirrored.Filled.ArrowForwardIos,
                                actionButtonDesc = stringResource(R.string.select_ssh_key)
                            )
                        }
                    }
                }
            }
        }

        if (uiState.isShowGenerateSshKeyDialog) {
            GenerateSshKeyDialog(
                onSave = { alias, algorithm ->
                    viewModel.updateShowGenerateSshKeyDialog(false)
                    viewModel.updateShowBottomSheet(false)
                    viewModel.generateSshKey(
                        algorithm = SshKeyGenerator.Algorithm.entries.first { it.title == algorithm },
                        alias = alias
                    )
                },
                onDismiss = { viewModel.updateShowGenerateSshKeyDialog(false) }
            )
        }
    }
}

@Composable
@Preview
fun AddHostPreview() {
    EditHostScreen(rememberNavController(), TopAppBarParams.PREVIEW)
}
