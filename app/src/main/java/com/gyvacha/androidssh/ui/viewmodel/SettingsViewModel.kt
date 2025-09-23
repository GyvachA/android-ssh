package com.gyvacha.androidssh.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gyvacha.androidssh.domain.model.SshKey
import com.gyvacha.androidssh.domain.usecase.DeleteSshKeyUseCase
import com.gyvacha.androidssh.ui.state.SettingsUiState
import com.gyvacha.androidssh.utils.FingerprintManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val deleteSshKeyUseCase: DeleteSshKeyUseCase,
    private val fingerprintManager: FingerprintManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            fingerprintManager.fingerprints.collect { hosts ->
                _uiState.update { it.copy(knownHosts = hosts) }
            }
        }
    }

    fun updateSshKeyExtended(newExtended: Boolean) {
        _uiState.update {
            it.copy(
                extendedSshKeys = newExtended
            )
        }
    }

    fun updateKnownHostExtended(newExtended: Boolean) {
        _uiState.update {
            it.copy(
                extendedKnownHosts = newExtended
            )
        }
    }

    fun deleteSshKey(sshKey: SshKey) {
        viewModelScope.launch {
            deleteSshKeyUseCase(sshKey)
        }
    }

    fun deleteKnownHost(host: String) {
        fingerprintManager.remove(host)
    }
}
