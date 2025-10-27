package com.gyvacha.androidssh.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gyvacha.androidssh.domain.model.ProxyConfig
import com.gyvacha.androidssh.domain.model.ProxySpec
import com.gyvacha.androidssh.domain.model.ProxyType
import com.gyvacha.androidssh.domain.model.Transport
import com.gyvacha.androidssh.domain.model.withPort
import com.gyvacha.androidssh.domain.model.withServer
import com.gyvacha.androidssh.domain.usecase.GetProxyConfigUseCase
import com.gyvacha.androidssh.domain.usecase.InsertConfigUseCase
import com.gyvacha.androidssh.domain.usecase.UpdateConfigUseCase
import com.gyvacha.androidssh.ui.state.EditProxyConfigUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProxyConfigViewModel @Inject constructor(
    private val getProxyConfigUseCase: GetProxyConfigUseCase,
    private val insertProxyConfigUseCase: InsertConfigUseCase,
    private val updateProxyConfigUseCase: UpdateConfigUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProxyConfigUiState())
    val uiState = _uiState.asStateFlow()

    fun updateProxyType(proxyType: ProxyType) {
        _uiState.update {
            it.copy(
                proxyConfig = it.proxyConfig.copy(
                    type = proxyType,
                    config = defaultConfigForType(proxyType)
                )
            )
        }
    }

    fun updateProxy(proxyConfig: ProxyConfig) {
        _uiState.update { it.copy(proxyConfig = proxyConfig).validate() }
    }

    fun updateConfig(newConfig: ProxySpec) {
        _uiState.update {
            it.copy(proxyConfig = it.proxyConfig.copy(config = newConfig)).validate()
        }
    }

    fun updateAlias(newAlias: String) {
        _uiState.update {
            it.copy(
                proxyConfig = it.proxyConfig.copy(
                    alias = newAlias
                )
            ).validate()
        }
    }

    fun updatePort(newPort: String) {
        val portInt = newPort.toIntOrNull() ?: return
        _uiState.update {
            it.copy(proxyConfig = it.proxyConfig.copy(config = it.proxyConfig.config.withPort(portInt))).validate()
        }
    }

    fun updateServer(newServer: String) {
        _uiState.update {
            it.copy(proxyConfig = it.proxyConfig.copy(config = it.proxyConfig.config.withServer(newServer))).validate()
        }
    }

    fun getProxyConfig(proxyConfigId: Long) {
        if (_uiState.value.proxyConfig.id == proxyConfigId) return
        viewModelScope.launch {
            getProxyConfigUseCase(proxyConfigId)?.let {
                updateProxy(it)
            }
        }
    }

    fun insertProxyConfig() {
        viewModelScope.launch {
            insertProxyConfigUseCase(_uiState.value.proxyConfig)
        }
    }

    fun updateProxyConfig() {
        viewModelScope.launch {
            updateProxyConfigUseCase(_uiState.value.proxyConfig)
        }
    }

    private fun defaultConfigForType(type: ProxyType): ProxySpec {
        return when (type) {
            ProxyType.VLESS -> ProxySpec.Vless(
                server = "",
                port = 443,
                uuid = "",
                transport = Transport.TCP
            )
            ProxyType.VMESS -> ProxySpec.Vmess(
                server = "",
                port = 443,
                uuid = "",
                alterId = 0,
                security = "auto",
                transport = Transport.TCP
            )
            ProxyType.TROJAN -> ProxySpec.Trojan(
                server = "",
                port = 443,
                password = ""
            )
            ProxyType.SHADOWSOCKS -> ProxySpec.Shadowsocks(
                server = "",
                port = 443,
                method = "",
                password = ""
            )
            ProxyType.SOCKS -> ProxySpec.Socks(
                server = "",
                port = 443
            )
            ProxyType.HTTP -> ProxySpec.Http(
                server = "",
                port = 443
            )
        }
    }
}
