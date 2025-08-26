package com.gyvacha.androidssh.ui.state

import com.gyvacha.androidssh.domain.model.PingResult

data class SingboxUiState(
    val expandedTopAppBarMenu: Boolean = false,
    val isNotificationPermissionGranted: Boolean = false,
    val isVPNPermissionGranted: Boolean = false,
    val requestPermission: Boolean = false,
    val pingResult: PingResult = PingResult.Idle
)
