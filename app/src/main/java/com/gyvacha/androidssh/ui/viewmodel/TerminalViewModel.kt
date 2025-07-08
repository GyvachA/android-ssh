package com.gyvacha.androidssh.ui.viewmodel

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
import kotlinx.coroutines.flow.catch
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
                .catch { err ->
                    appendOutputLine("Error: ${err.localizedMessage}")
                }
                .collect { output ->
                    appendOutputLine(output)
                }
        }
    }

    private fun appendOutputLine(line: String) {
        _uiState.update {
            it.copy(
                terminalOutput = it.terminalOutput + line
            )
        }
    }

    fun initSshConnect(hostId: Int) {
        viewModelScope.launch {
            val hostWithSshKey = getHostWithSshKeyUseCase(hostId)
            _uiState.update { it.copy(hostWithSshKey = hostWithSshKey) }

            runCatching {
                val welcomeText = when (hostWithSshKey.host.authType) {
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
                welcomeText?.catch { err ->
                    appendOutputLine("Error: ${err.localizedMessage}")
                }
                    ?.collect { output ->
                        appendOutputLine(output)
                    }
            }
                .onFailure { err ->
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