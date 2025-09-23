package com.gyvacha.androidssh.ui.state

import androidx.compose.ui.text.AnnotatedString
import com.gyvacha.androidssh.domain.model.Host
import com.gyvacha.androidssh.domain.model.HostWithSshKey
import com.gyvacha.androidssh.domain.model.SshAuthType

data class TerminalUiState(
    val terminalOutput: AnnotatedString = AnnotatedString(""),
    val terminalInput: String = "",
    val isLoading: Boolean = false,
    val hostWithSshKey: HostWithSshKey = HostWithSshKey(
        sshKey = null,
        host = Host(
            hostId = 0,
            hostNameOrIp = "",
            port = 0,
            alias = "",
            userName = "",
            authType = SshAuthType.SSH_KEY
        )
    ),
    val pendingHostKey: String? = null
)
