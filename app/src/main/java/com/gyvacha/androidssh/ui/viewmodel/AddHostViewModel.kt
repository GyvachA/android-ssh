package com.gyvacha.androidssh.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gyvacha.androidssh.domain.model.DatabaseResult
import com.gyvacha.androidssh.domain.model.Host
import com.gyvacha.androidssh.domain.usecase.GetSshKeysUseCase
import com.gyvacha.androidssh.domain.usecase.InsertHostUseCase
import com.gyvacha.androidssh.ui.components.TextFieldErrors
import com.gyvacha.androidssh.ui.state.AddHostUiState
import com.gyvacha.androidssh.ui.utils.ViewEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddHostViewModel @Inject constructor(
    private val insertHostUseCase: InsertHostUseCase,
    getSshKeysUseCase: GetSshKeysUseCase
) : ViewModel() {

    private val _eventFlow = MutableSharedFlow<ViewEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _uiState = MutableStateFlow(AddHostUiState())
    val uiState = _uiState.asStateFlow()

    val sshKeys = getSshKeysUseCase()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun updateShowBottomSheet(newState: Boolean) {
        _uiState.update {
            it.copy(
                isShowBottomSheet = newState
            )
        }
    }

    fun updateShowGenerateSshKeyDialog(newState: Boolean) {
        _uiState.update {
            it.copy(
                isShowGenerateSshKeyDialog = newState
            )
        }
    }

    fun updateAlias(newAlias: String, isError: TextFieldErrors?) {
        _uiState.update {
            it.copy(
                alias = newAlias,
                isAliasError = isError
            )
        }
        updateIsFormValid()
    }

    fun updateHostNameOrIp(newHostNameOrIp: String, isError: TextFieldErrors?) {
        _uiState.update {
            it.copy(
                hostNameOrIp = newHostNameOrIp,
                isHostNameOrIpError = isError
            )
        }
        updateIsFormValid()
    }

    fun updatePort(newPort: String, isError: TextFieldErrors?) {
        _uiState.update {
            it.copy(
                port = newPort.toInt(),
                isPortError = isError
            )
        }
    }

    fun updateUserName(newUserName: String, isError: TextFieldErrors?) {
        _uiState.update {
            it.copy(
                userName = newUserName,
                isUserNameError = isError
            )
        }
    }

    fun updatePassword(newPassword: String) {
        _uiState.update { it.copy(password = newPassword) }
    }

    fun updatePasswordVisibility(newVisibility: Boolean) {
        _uiState.update { it.copy(isPasswordVisible = newVisibility) }
    }

    fun insertHost() {
        viewModelScope.launch {
            val result = insertHostUseCase(
                Host(
                    alias = _uiState.value.alias,
                    hostNameOrIp = _uiState.value.hostNameOrIp,
                    port = _uiState.value.port,
                    userName = _uiState.value.userName,
                    password = _uiState.value.password
                )
            )
            when (result) {
                is DatabaseResult.Error -> {
                    _eventFlow.emit(ViewEvent.DatabaseExceptionCaught(result.err))
                }

                DatabaseResult.Success -> {
                    _eventFlow.emit(ViewEvent.HostInserted)
                    _eventFlow.emit(ViewEvent.NavigateUp)
                }
            }
        }
    }

    private fun updateIsFormValid() {
        val isFormValid = !(_uiState.value.isAliasError != null ||
                _uiState.value.isPortError != null ||
                _uiState.value.isHostNameOrIpError != null ||
                _uiState.value.isUserNameError != null)
        _uiState.update { it.copy(isFormValid = isFormValid) }
    }
}