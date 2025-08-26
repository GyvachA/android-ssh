package com.gyvacha.androidssh.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gyvacha.androidssh.domain.model.ProxyConfig
import com.gyvacha.androidssh.domain.usecase.DeleteConfigUseCase
import com.gyvacha.androidssh.domain.usecase.GenerateSingboxConfigFileUseCase
import com.gyvacha.androidssh.domain.usecase.GetConfigsUseCase
import com.gyvacha.androidssh.domain.usecase.InsertConfigUseCase
import com.gyvacha.androidssh.domain.usecase.ObserveSingboxLogsUseCase
import com.gyvacha.androidssh.domain.usecase.ObserveSingboxStateUseCase
import com.gyvacha.androidssh.domain.usecase.PingActiveProxyUseCase
import com.gyvacha.androidssh.domain.usecase.SetActiveConfigUseCase
import com.gyvacha.androidssh.domain.usecase.StartSingboxUseCase
import com.gyvacha.androidssh.domain.usecase.StopSingboxUseCase
import com.gyvacha.androidssh.ui.state.SingboxUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SingboxViewModel @Inject constructor(
    private val deleteConfigUseCase: DeleteConfigUseCase,
    private val insertConfigUseCase: InsertConfigUseCase,
    private val setActiveConfigUseCase: SetActiveConfigUseCase,
    private val startSingboxUseCase: StartSingboxUseCase,
    private val stopSingboxUseCase: StopSingboxUseCase,
    private val generateSingboxConfigFileUseCase: GenerateSingboxConfigFileUseCase,
    private val pingUseCase: PingActiveProxyUseCase,
    observeSingboxLogsUseCase: ObserveSingboxLogsUseCase,
    observeSingboxStateUseCase: ObserveSingboxStateUseCase,
    getConfigsUseCase: GetConfigsUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(SingboxUiState())
    val uiState = _uiState.asStateFlow()
    val singboxState = observeSingboxStateUseCase()
    val singboxLogs = observeSingboxLogsUseCase()
        .stateIn(viewModelScope, SharingStarted.Lazily, "")
    val configs = getConfigsUseCase()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun updateExpandedTopAppBarMenu(newExpanded: Boolean) {
        _uiState.update {
            it.copy(
                expandedTopAppBarMenu = newExpanded
            )
        }
    }

    fun requestPing() {
        viewModelScope.launch {
            val result = pingUseCase()
            _uiState.update {
                it.copy(
                    pingResult = result
                )
            }
        }
    }

    fun deleteConfig(config: ProxyConfig) {
        viewModelScope.launch {
            deleteConfigUseCase(config)
        }
    }

    fun insertConfig(config: ProxyConfig) {
        viewModelScope.launch {
            insertConfigUseCase(config)
        }
    }

    fun setActiveConfig(config: ProxyConfig) {
        viewModelScope.launch {
            setActiveConfigUseCase(config)
        }
    }

    fun startSingbox() {
        viewModelScope.launch {
            generateSingboxConfigFileUseCase()
            startSingboxUseCase()
        }
    }

    fun stopSingbox() {
        viewModelScope.launch {
            stopSingboxUseCase()
        }
    }

    fun updateIsNotificationPermGranted(newIsGranted: Boolean) {
        _uiState.update {
            it.copy(
                isNotificationPermissionGranted = newIsGranted
            )
        }
    }

    fun updateIsVPNPermGranted(newIsGranted: Boolean) {
        _uiState.update {
            it.copy(
                isVPNPermissionGranted = newIsGranted
            )
        }
    }

    fun updateRequestPermission(newRequest: Boolean) {
        _uiState.update {
            it.copy(
                requestPermission = newRequest
            )
        }
    }
}
