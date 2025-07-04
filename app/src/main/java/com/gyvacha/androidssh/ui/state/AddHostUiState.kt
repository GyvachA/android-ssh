package com.gyvacha.androidssh.ui.state

import com.gyvacha.androidssh.domain.model.Host
import com.gyvacha.androidssh.domain.model.HostWithSshKey
import com.gyvacha.androidssh.domain.model.SshAuthType
import com.gyvacha.androidssh.ui.components.TextFieldErrors

data class AddHostUiState(
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
    val isAliasError: TextFieldErrors? = null,
    val isHostNameOrIpError: TextFieldErrors? = null,
    val isPortError: TextFieldErrors? = null,
    val isUserNameError: TextFieldErrors? = null,
    val isFormValid: Boolean = false,
    val isShowBottomSheet: Boolean = false,
    val isShowGenerateSshKeyDialog: Boolean = false,
)
