package com.gyvacha.androidssh.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gyvacha.androidssh.domain.usecase.GetHostUseCase
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
    private val getHostUseCase: GetHostUseCase,
    private val connectViaPwdUseCase: SshConnectViaPwdUseCase,
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
        appendOutputLine(command)
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
            val host = getHostUseCase(hostId)
            _uiState.update { it.copy(host = host) }

            runCatching {
                connectViaPwdUseCase(
                    host.hostNameOrIp,
                    host.port,
                    host.userName,
                    host.password!!
                )
            }
                .onFailure { err ->
                    appendOutputLine("Connect to host: FAILURE")
                    appendOutputLine("Error: ${err.localizedMessage}")
                    Log.e("Ssh", err.localizedMessage, err)
                }
                .onSuccess {
                    appendOutputLine("Connect to host: SUCCESS")
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