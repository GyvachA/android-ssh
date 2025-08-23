package com.gyvacha.androidssh.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gyvacha.androidssh.domain.model.SshKey
import com.gyvacha.androidssh.domain.usecase.GetSshKeyUseCase
import com.gyvacha.androidssh.domain.usecase.InsertSshKeyUseCase
import com.gyvacha.androidssh.domain.usecase.UpdateSshKeyUseCase
import com.gyvacha.androidssh.ui.state.EditSshKeyUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditSshKeyViewModel @Inject constructor(
    private val updateSshKeyUseCase: UpdateSshKeyUseCase,
    private val insertSshKeyUseCase: InsertSshKeyUseCase,
    private val getSshKeyUseCase: GetSshKeyUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditSshKeyUiState())
    val uiState = _uiState.asStateFlow()

    fun updatePassphrase(newPassphrase: String) {
        _uiState.update {
            it.copy(
                sshKey = it.sshKey.copy(
                    passphrase = newPassphrase
                ),
            )
        }
        updateIsSaveButtonEnabled()
    }

    fun updateAlias(newAlias: String) {
        _uiState.update {
            it.copy(
                sshKey = it.sshKey.copy(
                    alias = newAlias
                ),
            )
        }
        updateIsSaveButtonEnabled()
    }

    fun updatePublicKey(newPublicKey: String) {
        _uiState.update {
            it.copy(
                sshKey = it.sshKey.copy(
                    publicKey = newPublicKey
                ),
            )
        }
        updateIsSaveButtonEnabled()
    }

    fun updatePrivateKey(newPrivateKey: String) {
        _uiState.update {
            it.copy(
                sshKey = it.sshKey.copy(
                    privateKey = newPrivateKey
                ),
            )
        }
        updateIsSaveButtonEnabled()
    }

    fun updatePassphraseVisible(newState: Boolean) {
        _uiState.update {
            it.copy(
                isPassphraseVisible = newState
            )
        }
    }

    fun updatePrivateKeyVisible(newState: Boolean) {
        _uiState.update {
            it.copy(
                isPrivateKeyVisible = newState
            )
        }
    }

    fun updateSshKey(sshKey: SshKey) {
        _uiState.update {
            it.copy(
                sshKey = sshKey
            )
        }
    }

    fun updateSshKeyLocal() {
        viewModelScope.launch {
            updateSshKeyUseCase(_uiState.value.sshKey)
        }
    }

    fun getSshKey(sshKeyId: Int) {
        viewModelScope.launch {
            val sshKey = getSshKeyUseCase(sshKeyId)
            updateSshKey(sshKey)
        }
    }

    fun insertSshKey() {
        viewModelScope.launch {
            insertSshKeyUseCase(_uiState.value.sshKey)
        }
    }

    private fun updateIsSaveButtonEnabled() {
        val isEnabled = _uiState.value.sshKey.privateKey.isNotBlank() &&
            _uiState.value.sshKey.publicKey.isNotBlank() &&
            _uiState.value.sshKey.alias.isNotBlank()
        _uiState.update {
            it.copy(
                isSaveButtonEnabled = isEnabled
            )
        }
    }
}
