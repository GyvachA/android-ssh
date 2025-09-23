package com.gyvacha.androidssh.ui.state

data class SettingsUiState(
    val extendedSshKeys: Boolean = false,
    val extendedKnownHosts: Boolean = false,
    val knownHosts: Map<String, String> = emptyMap()
)
