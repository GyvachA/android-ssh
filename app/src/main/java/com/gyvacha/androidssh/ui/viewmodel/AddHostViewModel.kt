package com.gyvacha.androidssh.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gyvacha.androidssh.domain.model.DatabaseResult
import com.gyvacha.androidssh.domain.model.Host
import com.gyvacha.androidssh.domain.model.SshAuthType
import com.gyvacha.androidssh.domain.model.SshKey
import com.gyvacha.androidssh.domain.usecase.GenerateSshKeyUseCase
import com.gyvacha.androidssh.domain.usecase.GetSshKeysUseCase
import com.gyvacha.androidssh.domain.usecase.InsertHostUseCase
import com.gyvacha.androidssh.domain.usecase.InsertSshKeyUseCase
import com.gyvacha.androidssh.ui.components.TextFieldErrors
import com.gyvacha.androidssh.ui.state.AddHostUiState
import com.gyvacha.androidssh.ui.utils.ViewEvent
import com.gyvacha.androidssh.utils.SshKeyGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddHostViewModel @Inject constructor(
    private val insertHostUseCase: InsertHostUseCase,
    private val insertSshKeyUseCase: InsertSshKeyUseCase,
    private val generateSshKeyUseCase: GenerateSshKeyUseCase,
    getSshKeysUseCase: GetSshKeysUseCase
) : ViewModel() {

    private val _eventFlow = MutableSharedFlow<ViewEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _uiState = MutableStateFlow(AddHostUiState())
    val uiState = _uiState.asStateFlow()

    val sshKeys = getSshKeysUseCase()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun generateSshKey(algorithm: SshKeyGenerator.Algorithm, alias: String, passphrase: String? = null) {
        viewModelScope.launch {
            runCatching {
                val sshKey = generateSshKeyUseCase(algorithm, passphrase).copy(alias = alias)
                val sshKeyId = insertSshKeyUseCase(sshKey)
                updateSshKey(SshKey(sshKeyId = sshKeyId.toInt(), alias = alias, publicKey = sshKey.publicKey))
            }
                .onSuccess {
                    _eventFlow.emit(ViewEvent.SshKeyCreated)
                }
                .onFailure { err ->
                    _eventFlow.emit(ViewEvent.SshKeyCreateFailure)
                    Log.e(AddHostViewModel::class.simpleName, err.localizedMessage, err)
                }
        }
    }

    fun updateShowBottomSheet(newState: Boolean) {
        _uiState.update {
            it.copy(
                isShowBottomSheet = newState
            )
        }
    }

    fun updateSshKey(sshKey: SshKey?) {
        _uiState.update {
            it.copy(
                sshKey = sshKey
            )
        }
    }

    fun updateShowGenerateSshKeyDialog(newState: Boolean) {
        _uiState.update {
            it.copy(
                isShowGenerateSshKeyDialog = newState
            )
        }
    }

    fun updateSshAuthType(newAuthType: SshAuthType) {
        _uiState.update {
            it.copy(
                sshAuthType = newAuthType
            )
        }
    }

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

    fun updatePasswordVisibility(newVisibility: Boolean) {
        _uiState.update { it.copy(isPasswordVisible = newVisibility) }
    }

    fun insertHost() {
        viewModelScope.launch {
            val result = insertHostUseCase(
                Host(
                    alias = _uiState.value.alias.trim(),
                    hostNameOrIp = _uiState.value.hostNameOrIp,
                    port = _uiState.value.port,
                    userName = _uiState.value.userName.trim(),
                    password = _uiState.value.password,
                    sshKey = _uiState.value.sshKey?.sshKeyId,
                    authType = _uiState.value.sshAuthType
                )
            )
            when (result) {
                is DatabaseResult.Error -> {
                    _eventFlow.emit(ViewEvent.DatabaseExceptionCaught(result.err))
                }

                DatabaseResult.Success -> {
                    _eventFlow.emit(ViewEvent.HostInserted)
                    _eventFlow.emit(ViewEvent.NavigateUp)
                }
            }
        }
    }

    private fun updateIsFormValid() {
        val isFormValid = _uiState.value.alias.isNotBlank() &&
                _uiState.value.port != 0 &&
                _uiState.value.hostNameOrIp.isNotBlank() &&
                _uiState.value.userName.isNotBlank()
        _uiState.update { it.copy(isFormValid = isFormValid) }
    }
}