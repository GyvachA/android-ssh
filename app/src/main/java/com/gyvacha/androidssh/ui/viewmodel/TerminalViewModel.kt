package com.gyvacha.androidssh.ui.viewmodel

import android.util.Log
import androidx.compose.ui.text.AnnotatedString
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gyvacha.androidssh.domain.model.SshAuthType
import com.gyvacha.androidssh.domain.usecase.GetHostWithSshKeyUseCase
import com.gyvacha.androidssh.domain.usecase.SshConnectViaKeyUseCase
import com.gyvacha.androidssh.domain.usecase.SshConnectViaPwdUseCase
import com.gyvacha.androidssh.domain.usecase.SshDisconnectUseCase
import com.gyvacha.androidssh.domain.usecase.SshExecuteCommandUseCase
import com.gyvacha.androidssh.ui.state.TerminalUiState
import com.gyvacha.androidssh.utils.FingerprintManager
import com.gyvacha.androidssh.utils.parseAnsiToAnnotatedString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CompletableDeferred
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
    private val disconnectUseCase: SshDisconnectUseCase,
    private val fingerprintManager: FingerprintManager
) : ViewModel() {
    private val outputBuilder = AnnotatedString.Builder()
    private val _uiState = MutableStateFlow(TerminalUiState())
    val uiState = _uiState.asStateFlow()

    private var hostKeyContinuation: CompletableDeferred<Boolean>? = null

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
            runCatching {
                executeCommandUseCase(command)
            }
                .onFailure { err ->
                    Log.e(this@TerminalViewModel::class.simpleName, err.localizedMessage, err)
                    err.localizedMessage?.let { appendOutputLine(it + "\n") }
                }
        }
    }

    private fun appendOutputLine(line: String) {
        outputBuilder.append(parseAnsiToAnnotatedString(line))
        _uiState.update {
            it.copy(terminalOutput = outputBuilder.toAnnotatedString())
        }
    }

    private fun updateIsLoading(newLoading: Boolean) {
        _uiState.update {
            it.copy(
                isLoading = newLoading
            )
        }
    }

    fun confirmHostKey(approved: Boolean) {
        _uiState.update { it.copy(pendingHostKey = null) }
        hostKeyContinuation?.complete(approved)
        hostKeyContinuation = null
    }

    private suspend fun waitForUserDecision(): Boolean {
        hostKeyContinuation = CompletableDeferred()
        return hostKeyContinuation!!.await()
    }

    fun initSshConnect(hostId: Int) {
        viewModelScope.launch {
            runCatching {
                updateIsLoading(true)
                val hostWithSshKey = getHostWithSshKeyUseCase(hostId)
                val hostKeyId = "${hostWithSshKey.host.hostNameOrIp}:${hostWithSshKey.host.port}"
                _uiState.update { it.copy(hostWithSshKey = hostWithSshKey) }
                val outputFlow = when (hostWithSshKey.host.authType) {
                    SshAuthType.PASSWORD -> {
                        connectViaPwdUseCase(
                            hostWithSshKey.host.hostNameOrIp,
                            hostWithSshKey.host.port,
                            hostWithSshKey.host.userName,
                            hostWithSshKey.host.password ?: ""
                        ) { fingerprint ->
                            handleHostKey(hostKeyId, fingerprint)
                        }
                    }
                    SshAuthType.SSH_KEY -> {
                        connectViaSshKeyUseCase(
                            hostWithSshKey.host.hostNameOrIp,
                            hostWithSshKey.host.port,
                            hostWithSshKey.host.userName,
                            hostWithSshKey.sshKey?.privateKey ?: "",
                            hostWithSshKey.sshKey?.publicKey ?: "",
                            hostWithSshKey.sshKey?.passphrase,
                        ) { fingerprint ->
                            handleHostKey(hostKeyId, fingerprint)
                        }
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

    private suspend fun handleHostKey(host: String, fingerprint: String): Boolean {
        return if (fingerprintManager.isKnown(host, fingerprint)) {
            true
        } else {
            _uiState.update { it.copy(pendingHostKey = fingerprint) }
            val approved = waitForUserDecision()
            if (approved) fingerprintManager.add(host, fingerprint)
            approved
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            disconnectUseCase()
        }
    }
}
