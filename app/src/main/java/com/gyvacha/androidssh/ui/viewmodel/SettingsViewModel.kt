package com.gyvacha.androidssh.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gyvacha.androidssh.domain.usecase.GetSshKeysUseCase
import com.gyvacha.androidssh.ui.state.SettingsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    getSshKeysUseCase: GetSshKeysUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState = _uiState.asStateFlow()

    val sshKeys = getSshKeysUseCase()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun updateSshKeyExtended(newExtended: Boolean) {
        _uiState.update {
            it.copy(
                extendedSshKeys = newExtended
            )
        }
    }
}