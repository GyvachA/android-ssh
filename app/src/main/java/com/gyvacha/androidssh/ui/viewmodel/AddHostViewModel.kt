package com.gyvacha.androidssh.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gyvacha.androidssh.domain.model.Host
import com.gyvacha.androidssh.domain.usecase.InsertHostUseCase
import com.gyvacha.androidssh.ui.components.TextFieldErrors
import com.gyvacha.androidssh.ui.state.AddHostUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddHostViewModel @Inject constructor(
    private val insertHostUseCase: InsertHostUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddHostUiState())
    val uiState = _uiState.asStateFlow()

    fun updateAlias(newAlias: String, isError: TextFieldErrors?) {
        _uiState.update {
            it.copy(
                alias = newAlias,
                isAliasError = isError
            )
        }
        updateIsFormValid()
    }

    fun updateHostNameOrIp(newHostNameOrIp: String, isError: TextFieldErrors?) {
        _uiState.update {
            it.copy(
                hostNameOrIp = newHostNameOrIp,
                isHostNameOrIpError = isError
            )
        }
        updateIsFormValid()
    }

    fun updatePort(newPort: String, isError: TextFieldErrors?) {
        _uiState.update {
            it.copy(
                port = newPort.toInt(),
                isPortError = isError
            )
        }
    }

    fun updateUserName(newUserName: String, isError: TextFieldErrors?) {
        _uiState.update {
            it.copy(
                userName = newUserName,
                isUserNameError = isError
            )
        }
    }

    fun updatePassword(newPassword: String) {
        _uiState.update { it.copy(password = newPassword) }
    }

    fun updateSshKey(newSshKey: String) {
        _uiState.update { it.copy(sshKey = newSshKey.toInt()) }
    }

    fun updatePasswordVisibility(newVisibility: Boolean) {
        _uiState.update { it.copy(isPasswordVisible = newVisibility) }
    }

    fun insertHost() {
        viewModelScope.launch {
            insertHostUseCase(
                Host(
                    alias = _uiState.value.alias,
                    hostNameOrIp = _uiState.value.hostNameOrIp,
                    port = _uiState.value.port,
                    userName = _uiState.value.userName,
                    password = _uiState.value.password
                )
            )
        }
    }

    private fun updateIsFormValid() {
        val isFormValid = !(_uiState.value.isAliasError != null ||
                _uiState.value.isPortError != null ||
                _uiState.value.isHostNameOrIpError != null ||
                _uiState.value.isUserNameError != null)
        _uiState.update { it.copy(isFormValid = isFormValid) }
    }
}