package com.gyvacha.androidssh.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gyvacha.androidssh.domain.model.Host
import com.gyvacha.androidssh.domain.model.HostWithSshKey
import com.gyvacha.androidssh.domain.model.SshAuthType
import com.gyvacha.androidssh.domain.model.SshKey
import com.gyvacha.androidssh.domain.usecase.GetHostWithSshKeyUseCase
import com.gyvacha.androidssh.domain.usecase.InsertHostUseCase
import com.gyvacha.androidssh.domain.usecase.UpdateHostUseCase
import com.gyvacha.androidssh.ui.state.EditHostUiState
import com.gyvacha.androidssh.ui.utils.EditHostViewEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditHostViewModel @Inject constructor(
    private val insertHostUseCase: InsertHostUseCase,
    private val updateHostUseCase: UpdateHostUseCase,
    private val getHostWithSshKeyUseCase: GetHostWithSshKeyUseCase
) : ViewModel() {

    private val _eventFlow = MutableSharedFlow<EditHostViewEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _uiState = MutableStateFlow(EditHostUiState())
    val uiState = _uiState.asStateFlow()

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
            ).validate()
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
            ).validate()
        }
    }

    fun updateAlias(newAlias: String) {
        _uiState.update {
            it.copy(
                hostWithSshKey = it.hostWithSshKey.copy(
                    host = it.hostWithSshKey.host.copy(
                        alias = newAlias
                    )
                )
            ).validate()
        }
    }

    fun updateHostNameOrIp(newHostNameOrIp: String) {
        _uiState.update {
            it.copy(
                hostWithSshKey = it.hostWithSshKey.copy(
                    host = it.hostWithSshKey.host.copy(
                        hostNameOrIp = newHostNameOrIp
                    )
                )
            ).validate()
        }
    }

    fun updatePort(newPort: String) {
        _uiState.update {
            it.copy(
                hostWithSshKey = it.hostWithSshKey.copy(
                    host = it.hostWithSshKey.host.copy(
                        port = newPort.toInt()
                    )
                )
            ).validate()
        }
    }

    fun updateUserName(newUserName: String) {
        _uiState.update {
            it.copy(
                hostWithSshKey = it.hostWithSshKey.copy(
                    host = it.hostWithSshKey.host.copy(
                        userName = newUserName
                    )
                )
            ).validate()
        }
    }

    fun updatePassword(newPassword: String) {
        _uiState.update {
            it.copy(
                hostWithSshKey = it.hostWithSshKey.copy(
                    host = it.hostWithSshKey.host.copy(
                        password = newPassword
                    )
                ),
            ).validate()
        }
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
                    _eventFlow.emit(EditHostViewEvent.DatabaseExceptionCaught)
                }
                .onSuccess {
                    _eventFlow.emit(EditHostViewEvent.HostUpdated)
                    _eventFlow.emit(EditHostViewEvent.NavigateUp)
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
                    _eventFlow.emit(EditHostViewEvent.DatabaseExceptionCaught)
                }
                .onSuccess {
                    _eventFlow.emit(EditHostViewEvent.HostInserted)
                    _eventFlow.emit(EditHostViewEvent.NavigateUp)
                }
        }
    }

    private fun updateHostWithSshKey(newHostWithSshKey: HostWithSshKey) {
        _uiState.update {
            it.copy(hostWithSshKey = newHostWithSshKey).validate()
        }
    }
}
