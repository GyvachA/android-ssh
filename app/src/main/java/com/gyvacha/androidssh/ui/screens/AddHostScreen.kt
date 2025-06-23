package com.gyvacha.androidssh.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.gyvacha.androidssh.domain.model.navigation.TopAppBarParams
import com.gyvacha.androidssh.ui.components.BottomFabSaveActions
import com.gyvacha.androidssh.ui.components.SecureTextField
import com.gyvacha.androidssh.ui.components.TextFieldBase
import com.gyvacha.androidssh.ui.components.TextFieldCharacterCount
import com.gyvacha.androidssh.ui.components.TextFieldErrors
import com.gyvacha.androidssh.ui.components.TopAppBarWithBackButton
import com.gyvacha.androidssh.ui.components.getTextFieldErrorMessage
import com.gyvacha.androidssh.ui.utils.ViewEvent
import com.gyvacha.androidssh.ui.viewmodel.AddHostViewModel
import com.gyvacha.androidssh.utils.LocalMessageNotifier

@Composable
fun AddHostScreen(
    navController: NavController,
    topAppBarParams: TopAppBarParams,
    modifier: Modifier = Modifier,
    viewModel: AddHostViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    val messageNotifier = LocalMessageNotifier.current
    val messageHostCreated = stringResource(R.string.host_added_succesfully)
    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is ViewEvent.DatabaseExceptionCaught -> messageNotifier?.showSnackbar("Ошибка добавления хоста")
                ViewEvent.HostInserted -> messageNotifier?.showSnackbar(messageHostCreated)
                ViewEvent.NavigateUp -> navController.navigateUp()
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
                    isSaveButtonActive = uiState.value.isFormValid,
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
                value = uiState.value.alias,
                onValueChange = {
                    val newAlias = it.trim()
                    var isError: TextFieldErrors? = null
                    if (newAlias.length > maxTextLength) isError = TextFieldErrors.STRING_LENGTH_ERROR
                    if (newAlias.isBlank()) isError = TextFieldErrors.STRING_BLANK_ERROR
                    viewModel.updateAlias(newAlias, isError)
                },
                isError = uiState.value.isAliasError != null,
                errorMessage = getTextFieldErrorMessage(uiState.value.isAliasError),
                label = { Text(text = stringResource(R.string.alias)) },
                maxLength = maxTextLength
            )
            Spacer(Modifier.padding(dimensionResource(R.dimen.small_padding)))
            TextFieldCharacterCount(
                value = uiState.value.hostNameOrIp,
                onValueChange = {
                    val newHostNameOrIp = it.trim()
                    var isError: TextFieldErrors? = null
                    if (newHostNameOrIp.length > maxTextLength) isError = TextFieldErrors.STRING_LENGTH_ERROR
                    if (newHostNameOrIp.isBlank()) isError = TextFieldErrors.STRING_BLANK_ERROR
                    viewModel.updateHostNameOrIp(newHostNameOrIp, isError)
                },
                isError = uiState.value.isHostNameOrIpError != null,
                errorMessage = getTextFieldErrorMessage(uiState.value.isHostNameOrIpError),
                label = { Text(text = stringResource(R.string.address)) },
                maxLength = maxTextLength
            )
            Spacer(Modifier.padding(dimensionResource(R.dimen.small_padding)))
            TextFieldCharacterCount(
                value = uiState.value.port.toString(),
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
                isError = uiState.value.isPortError != null,
                errorMessage = getTextFieldErrorMessage(uiState.value.isPortError)
            )
            Spacer(Modifier.padding(dimensionResource(R.dimen.small_padding)))
            TextFieldCharacterCount(
                value = uiState.value.userName,
                onValueChange = {
                    val newUsername = it.trim()
                    var isError: TextFieldErrors? = null
                    if (newUsername.length > maxTextLength) isError = TextFieldErrors.STRING_LENGTH_ERROR
                    if (newUsername.isBlank()) isError = TextFieldErrors.STRING_BLANK_ERROR
                    viewModel.updateUserName(
                        newUsername,
                        isError
                    )
                },
                label = { Text(text = stringResource(R.string.user_name)) },
                maxLength = maxTextLength,
                isError = uiState.value.isUserNameError != null,
                errorMessage = getTextFieldErrorMessage(uiState.value.isUserNameError)
            )
            Spacer(Modifier.padding(dimensionResource(R.dimen.small_padding)))
            SecureTextField(
                value = uiState.value.password,
                onValueChange = viewModel::updatePassword,
                label = stringResource(R.string.password),
                onVisibilityClick = { viewModel.updatePasswordVisibility(!uiState.value.isPasswordVisible) },
                isPasswordVisible = uiState.value.isPasswordVisible
            )
            Spacer(Modifier.padding(dimensionResource(R.dimen.small_padding)))
            TextFieldBase(
                value = uiState.value.sshKey.toString(),
                onValueChange = viewModel::updateSshKey,
                label = { Text(text = stringResource(R.string.ssh_key)) },
            )
        }
    }
}

@Composable
@Preview
fun AddHostPreview() {
    AddHostScreen(rememberNavController(), TopAppBarParams.PREVIEW)
}
