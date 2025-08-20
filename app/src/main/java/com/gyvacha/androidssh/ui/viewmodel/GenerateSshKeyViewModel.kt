package com.gyvacha.androidssh.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gyvacha.androidssh.domain.model.SshKey
import com.gyvacha.androidssh.domain.usecase.GenerateSshKeyUseCase
import com.gyvacha.androidssh.domain.usecase.InsertSshKeyUseCase
import com.gyvacha.androidssh.ui.state.GenerateSshKeyUiState
import com.gyvacha.androidssh.ui.utils.GenerateSshKeyViewEvent
import com.gyvacha.androidssh.utils.SshKeyGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GenerateSshKeyViewModel @Inject constructor(
    private val generateSshKeyUseCase: GenerateSshKeyUseCase,
    private val insertSshKeyUseCase: InsertSshKeyUseCase,
) : ViewModel() {
    val _uiState = MutableStateFlow(GenerateSshKeyUiState())
    val uiState = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<GenerateSshKeyViewEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun updateSshKeyAlias(newAlias: String) {
        _uiState.update {
            it.copy(sshKeyAlias = newAlias)
        }
    }

    fun updateSshKeyPassphrase(newPassphrase: String) {
        _uiState.update {
            it.copy(sshKeyPassphrase = newPassphrase)
        }
    }

    fun updateSshKeyAlgorithm(newAlgorithm: String) {
        _uiState.update {
            it.copy(sshKeyAlgorithm = newAlgorithm)
        }
    }

    fun updateSshKeyAlgorithmMenuExpanded(newState: Boolean) {
        _uiState.update {
            it.copy(sshKeyAlgorithmMenuExpanded = newState)
        }
    }

    fun updateSaveButtonEnabled(newState: Boolean) {
        _uiState.update {
            it.copy(saveButtonEnabled = newState)
        }
    }

    fun updatePassphraseVisible(newState: Boolean) {
        _uiState.update {
            it.copy(isPassphraseVisible = newState)
        }
    }

    fun generateSshKey(onSaveSshKey: (SshKey) -> Unit = {}) {
        viewModelScope.launch {
            runCatching {
                val sshKey = generateSshKeyUseCase(
                    algorithm = SshKeyGenerator.Algorithm.entries.first { it.title == _uiState.value.sshKeyAlgorithm },
                    passphrase = _uiState.value.sshKeyPassphrase.ifBlank {
                        null
                    }
                )
                    .copy(alias = _uiState.value.sshKeyAlias)
                val sshKeyId = insertSshKeyUseCase(sshKey)
                onSaveSshKey(
                    SshKey(
                        sshKeyId = sshKeyId.toInt(),
                        alias = _uiState.value.sshKeyAlias,
                        publicKey = sshKey.publicKey,
                        privateKey = sshKey.privateKey
                    )
                )
            }
                .onSuccess {
                    _eventFlow.emit(GenerateSshKeyViewEvent.SshKeyCreated)
                }
                .onFailure { err ->
                    _eventFlow.emit(GenerateSshKeyViewEvent.SshKeyCreateFailure)
                }
        }
    }
}