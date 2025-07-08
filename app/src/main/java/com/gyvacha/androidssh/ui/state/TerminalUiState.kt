package com.gyvacha.androidssh.ui.state

import com.gyvacha.androidssh.domain.model.Host
import com.gyvacha.androidssh.domain.model.HostWithSshKey
import com.gyvacha.androidssh.domain.model.SshAuthType

data class TerminalUiState(
    val terminalOutput: List<String> = emptyList(),
    val terminalInput: String = "",
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
    )
)
