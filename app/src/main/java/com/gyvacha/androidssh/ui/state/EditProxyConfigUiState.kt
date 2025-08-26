package com.gyvacha.androidssh.ui.state

import com.gyvacha.androidssh.domain.model.ProxyConfig
import com.gyvacha.androidssh.domain.model.ProxySpec
import com.gyvacha.androidssh.domain.model.ProxyType

data class EditProxyConfigUiState(
    val isFormValid: Boolean = false,
    val proxyConfig: ProxyConfig = ProxyConfig(
        id = 0,
        alias = "",
        type = ProxyType.VLESS,
        isActive = false,
        config = ProxySpec.Vless(
            server = "",
            port = 443,
            uuid = ""
        )
    )
) {
    fun validate(): EditProxyConfigUiState {
        val cfg = proxyConfig.config
        val aliasValid = proxyConfig.alias.isNotBlank()
        val serverValid = cfg.server.isNotBlank()
        val portValid = cfg.port.toString().length <= PORT_MAX_LEN

        val typeValid = when (cfg) {
            is ProxySpec.Vless -> cfg.uuid.isNotBlank()
            is ProxySpec.Vmess -> cfg.uuid.isNotBlank()
            is ProxySpec.Trojan -> cfg.password.isNotBlank()
            is ProxySpec.Shadowsocks -> cfg.password.isNotBlank() && cfg.method.isNotBlank()
            is ProxySpec.Socks -> true
            is ProxySpec.Http -> true
        }

        return copy(
            isFormValid = aliasValid && serverValid && portValid && typeValid
        )
    }

    companion object {
        private const val PORT_MAX_LEN = 5
    }
}
