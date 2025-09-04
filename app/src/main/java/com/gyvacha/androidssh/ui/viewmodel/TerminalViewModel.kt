package com.gyvacha.androidssh.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gyvacha.androidssh.domain.model.SshAuthType
import com.gyvacha.androidssh.domain.usecase.GetHostWithSshKeyUseCase
import com.gyvacha.androidssh.domain.usecase.SshConnectViaKeyUseCase
import com.gyvacha.androidssh.domain.usecase.SshConnectViaPwdUseCase
import com.gyvacha.androidssh.domain.usecase.SshDisconnectUseCase
import com.gyvacha.androidssh.domain.usecase.SshExecuteCommandUseCase
import com.gyvacha.androidssh.ui.state.TerminalUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TerminalViewModel @Inject constructor(
    private val executeCommandUseCase: SshExecuteCommandUseCase,
    private val getHostWithSshKeyUseCase: GetHostWithSshKeyUseCase,
    private val connectViaPwdUseCase: SshConnectViaPwdUseCase,
    private val connectViaSshKeyUseCase: SshConnectViaKeyUseCase,
    private val disconnectUseCase: SshDisconnectUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TerminalUiState())
    val uiState = _uiState.asStateFlow()

    fun updateTerminalInput(newInput: String) {
        _uiState.update {
            it.copy(terminalInput = newInput)
        }
    }

    fun sendCommand() {
        val command = _uiState.value.terminalInput.trim()
        _uiState.update {
            it.copy(terminalInput = "")
        }
        viewModelScope.launch {
            executeCommandUseCase(command)
        }
    }

    private fun appendOutputLine(line: String) {
        _uiState.update {
            it.copy(
                terminalOutput = it.terminalOutput + line
            )
        }
    }

    private fun updateIsLoading(newLoading: Boolean) {
        _uiState.update {
            it.copy(
                isLoading = newLoading
            )
        }
    }

    fun initSshConnect(hostId: Int) {
        viewModelScope.launch {
            runCatching {
                updateIsLoading(true)
                val hostWithSshKey = getHostWithSshKeyUseCase(hostId)
                _uiState.update { it.copy(hostWithSshKey = hostWithSshKey) }
                val outputFlow = when (hostWithSshKey.host.authType) {
                    SshAuthType.PASSWORD -> {
                        connectViaPwdUseCase(
                            hostWithSshKey.host.hostNameOrIp,
                            hostWithSshKey.host.port,
                            hostWithSshKey.host.userName,
                            hostWithSshKey.host.password ?: ""
                        )
                    }
                    SshAuthType.SSH_KEY -> {
                        connectViaSshKeyUseCase(
                            hostWithSshKey.host.hostNameOrIp,
                            hostWithSshKey.host.port,
                            hostWithSshKey.host.userName,
                            hostWithSshKey.sshKey?.privateKey ?: "",
                            hostWithSshKey.sshKey?.publicKey ?: "",
                            hostWithSshKey.sshKey?.passphrase,
                        )
                    }
                }
                updateIsLoading(false)
                outputFlow
                    .collect { output ->
                        appendOutputLine(output)
                    }
            }
                .onSuccess {
                    updateIsLoading(false)
                }
                .onFailure { err ->
                    Log.e(this::class.simpleName, err.localizedMessage, err)
                    updateIsLoading(false)
                    appendOutputLine("Error: ${err.localizedMessage}")
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            disconnectUseCase()
        }
    }
}
