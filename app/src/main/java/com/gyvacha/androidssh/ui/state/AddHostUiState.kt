package com.gyvacha.androidssh.ui.state

import com.gyvacha.androidssh.domain.model.SshAuthType
import com.gyvacha.androidssh.domain.model.SshKey
import com.gyvacha.androidssh.ui.components.TextFieldErrors

data class AddHostUiState(
    val alias: String = "Host",
    val hostNameOrIp: String = "",
    val port: Int = 22,
    val userName: String = "",
    val password: String = "",
    val sshKey: SshKey? = null,
    val isPasswordVisible: Boolean = false,
    val isAliasError: TextFieldErrors? = null,
    val isHostNameOrIpError: TextFieldErrors? = null,
    val isPortError: TextFieldErrors? = null,
    val isUserNameError: TextFieldErrors? = null,
    val isFormValid: Boolean = false,
    val isShowBottomSheet: Boolean = false,
    val isShowGenerateSshKeyDialog: Boolean = false,
    val sshAuthType: SshAuthType = SshAuthType.PASSWORD
)
