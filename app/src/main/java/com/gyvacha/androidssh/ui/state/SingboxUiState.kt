package com.gyvacha.androidssh.ui.state

data class SingboxUiState(
    val expandedTopAppBarMenu: Boolean = false,
    val isNotificationPermissionGranted: Boolean = false,
    val isVPNPermissionGranted: Boolean = false,
    val requestPermission: Boolean = false,
)