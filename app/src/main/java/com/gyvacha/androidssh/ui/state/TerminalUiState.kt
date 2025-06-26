package com.gyvacha.androidssh.ui.state

import com.gyvacha.androidssh.domain.model.Host

data class TerminalUiState(
    val terminalOutput: List<String> = listOf("Welcome to Terminal!"),
    val terminalInput: String = "",
    val host: Host = Host(
        hostId = 0,
        hostNameOrIp = "",
        port = 0,
        alias = "",
        userName = ""
    )
)
