package com.gyvacha.androidssh.ui.state

import com.gyvacha.androidssh.domain.model.Host
import com.gyvacha.androidssh.domain.model.HostWithSshKey
import com.gyvacha.androidssh.domain.model.SshAuthType

data class EditHostUiState(
    val hostWithSshKey: HostWithSshKey = HostWithSshKey(
        host = Host(
            hostId = -1,
            alias = "Host",
            hostNameOrIp = "",
            port = 22,
            userName = "",
            password = "",
            authType = SshAuthType.PASSWORD
        ),
        sshKey = null
    ),
    val isPasswordVisible: Boolean = false,
    val isFormValid: Boolean = false,
    val isShowBottomSheet: Boolean = false,
    val isShowGenerateSshKeyDialog: Boolean = false,
) {
    fun validate(): EditHostUiState {
        val aliasValid = hostWithSshKey.host.alias.isNotBlank()
        val serverValid = hostWithSshKey.host.hostNameOrIp.isNotBlank()
        val userNameValid = hostWithSshKey.host.userName.isNotBlank()
        return copy(
            isFormValid = aliasValid && serverValid && userNameValid
        )
    }
}
