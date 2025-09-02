package com.gyvacha.androidssh.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gyvacha.androidssh.domain.model.SshKey
import com.gyvacha.androidssh.domain.usecase.DeleteSshKeyUseCase
import com.gyvacha.androidssh.ui.state.SettingsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val deleteSshKeyUseCase: DeleteSshKeyUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState = _uiState.asStateFlow()

    fun updateSshKeyExtended(newExtended: Boolean) {
        _uiState.update {
            it.copy(
                extendedSshKeys = newExtended
            )
        }
    }

    fun deleteSshKey(sshKey: SshKey) {
        viewModelScope.launch {
            deleteSshKeyUseCase(sshKey)
        }
    }
}
