package com.gyvacha.androidssh.ui.state

data class SettingsUiState(
    val extendedSshKeys: Boolean = false,
    val isShowGenerateSshKeyDialog: Boolean = false,
    val isShowAddSshKeyDialog: Boolean = false,
    val editSshKeyId: Int? = null,
)
