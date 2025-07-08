package com.gyvacha.androidssh.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gyvacha.androidssh.domain.model.Host
import com.gyvacha.androidssh.domain.model.HostWithSshKey
import com.gyvacha.androidssh.domain.model.SshAuthType
import com.gyvacha.androidssh.domain.model.SshKey
import com.gyvacha.androidssh.domain.usecase.GenerateSshKeyUseCase
import com.gyvacha.androidssh.domain.usecase.GetHostWithSshKeyUseCase
import com.gyvacha.androidssh.domain.usecase.GetSshKeysUseCase
import com.gyvacha.androidssh.domain.usecase.InsertHostUseCase
import com.gyvacha.androidssh.domain.usecase.InsertSshKeyUseCase
import com.gyvacha.androidssh.domain.usecase.UpdateHostUseCase
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
class EditHostViewModel @Inject constructor(
    private val insertHostUseCase: InsertHostUseCase,
    private val updateHostUseCase: UpdateHostUseCase,
    private val insertSshKeyUseCase: InsertSshKeyUseCase,
    private val generateSshKeyUseCase: GenerateSshKeyUseCase,
    private val getHostWithSshKeyUseCase: GetHostWithSshKeyUseCase,
    getSshKeysUseCase: GetSshKeysUseCase
) : ViewModel() {

    private val _eventFlow = MutableSharedFlow<ViewEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _uiState = MutableStateFlow(AddHostUiState())
    val uiState = _uiState.asStateFlow()

    val sshKeys = getSshKeysUseCase()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun generateSshKey(
        algorithm: SshKeyGenerator.Algorithm,
        alias: String,
        passphrase: String? = null
    ) {
        viewModelScope.launch {
            runCatching {
                val sshKey = generateSshKeyUseCase(algorithm, passphrase).copy(alias = alias)
                val sshKeyId = insertSshKeyUseCase(sshKey)
                updateSshKey(
                    SshKey(
                        sshKeyId = sshKeyId.toInt(),
                        alias = alias,
                        publicKey = sshKey.publicKey,
                        privateKey = sshKey.privateKey
                    )
                )
            }
                .onSuccess {
                    _eventFlow.emit(ViewEvent.SshKeyCreated)
                }
                .onFailure { err ->
                    _eventFlow.emit(ViewEvent.SshKeyCreateFailure)
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
                hostWithSshKey = it.hostWithSshKey.copy(
                    sshKey = sshKey
                ),
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
                hostWithSshKey = it.hostWithSshKey.copy(
                    host = it.hostWithSshKey.host.copy(
                        authType = newAuthType
                    )
                ),
            )
        }
    }

    fun updateAlias(newAlias: String, isError: TextFieldErrors?) {
        _uiState.update {
            it.copy(
                hostWithSshKey = it.hostWithSshKey.copy(
                    host = it.hostWithSshKey.host.copy(
                        alias = newAlias
                    )
                ),
                isAliasError = isError
            )
        }
        updateIsFormValid()
    }

    fun updateHostNameOrIp(newHostNameOrIp: String, isError: TextFieldErrors?) {
        _uiState.update {
            it.copy(
                hostWithSshKey = it.hostWithSshKey.copy(
                    host = it.hostWithSshKey.host.copy(
                        hostNameOrIp = newHostNameOrIp
                    )
                ),
                isHostNameOrIpError = isError
            )
        }
        updateIsFormValid()
    }

    fun updatePort(newPort: String, isError: TextFieldErrors?) {
        _uiState.update {
            it.copy(
                hostWithSshKey = it.hostWithSshKey.copy(
                    host = it.hostWithSshKey.host.copy(
                        port = newPort.toInt()
                    )
                ),
                isPortError = isError
            )
        }
        updateIsFormValid()
    }

    fun updateUserName(newUserName: String, isError: TextFieldErrors?) {
        _uiState.update {
            it.copy(
                hostWithSshKey = it.hostWithSshKey.copy(
                    host = it.hostWithSshKey.host.copy(
                        userName = newUserName
                    )
                ),
                isUserNameError = isError
            )
        }
        updateIsFormValid()
    }

    fun updatePassword(newPassword: String) {
        _uiState.update {
            it.copy(
                hostWithSshKey = it.hostWithSshKey.copy(
                    host = it.hostWithSshKey.host.copy(
                        password = newPassword
                    )
                ),
            )
        }
        updateIsFormValid()
    }


    fun getHostWithSshKey(newHostId: Int) {
        viewModelScope.launch {
            val hostWithSshKey = getHostWithSshKeyUseCase(newHostId)
            updateHostWithSshKey(hostWithSshKey)
        }
    }

    fun updatePasswordVisibility(newVisibility: Boolean) {
        _uiState.update { it.copy(isPasswordVisible = newVisibility) }
    }

    fun updateHost() {
        viewModelScope.launch {
            runCatching {
                updateHostUseCase(
                    Host(
                        hostId = _uiState.value.hostWithSshKey.host.hostId,
                        alias = _uiState.value.hostWithSshKey.host.alias.trim(),
                        hostNameOrIp = _uiState.value.hostWithSshKey.host.hostNameOrIp,
                        port = _uiState.value.hostWithSshKey.host.port,
                        userName = _uiState.value.hostWithSshKey.host.userName.trim(),
                        password = _uiState.value.hostWithSshKey.host.password,
                        sshKey = _uiState.value.hostWithSshKey.sshKey?.sshKeyId,
                        authType = _uiState.value.hostWithSshKey.host.authType
                    )
                )
            }
                .onFailure { err ->
                    _eventFlow.emit(ViewEvent.DatabaseExceptionCaught)
                }
                .onSuccess {
                    _eventFlow.emit(ViewEvent.HostUpdated)
                    _eventFlow.emit(ViewEvent.NavigateUp)
                }
        }
    }

    fun insertHost() {
        viewModelScope.launch {
            runCatching {
                insertHostUseCase(
                    Host(
                        alias = _uiState.value.hostWithSshKey.host.alias.trim(),
                        hostNameOrIp = _uiState.value.hostWithSshKey.host.hostNameOrIp,
                        port = _uiState.value.hostWithSshKey.host.port,
                        userName = _uiState.value.hostWithSshKey.host.userName.trim(),
                        password = _uiState.value.hostWithSshKey.host.password,
                        sshKey = _uiState.value.hostWithSshKey.sshKey?.sshKeyId,
                        authType = _uiState.value.hostWithSshKey.host.authType
                    )
                )
            }
                .onFailure { err ->
                    Log.e(EditHostViewModel::class.simpleName, err.localizedMessage, err)
                    _eventFlow.emit(ViewEvent.DatabaseExceptionCaught)
                }
                .onSuccess {
                    _eventFlow.emit(ViewEvent.HostInserted)
                    _eventFlow.emit(ViewEvent.NavigateUp)
                }
        }
    }

    private fun updateHostWithSshKey(newHostWithSshKey: HostWithSshKey) {
        _uiState.update {
            it.copy(hostWithSshKey = newHostWithSshKey)
        }
    }

    private fun updateIsFormValid() {
        val isFormValid = _uiState.value.hostWithSshKey.host.alias.isNotBlank() &&
                _uiState.value.hostWithSshKey.host.port != 0 &&
                _uiState.value.hostWithSshKey.host.hostNameOrIp.isNotBlank() &&
                _uiState.value.hostWithSshKey.host.userName.isNotBlank()
        _uiState.update { it.copy(isFormValid = isFormValid) }
    }
}