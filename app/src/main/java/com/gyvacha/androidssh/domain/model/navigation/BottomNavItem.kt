package com.gyvacha.androidssh.domain.model.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.ui.graphics.vector.ImageVector
import com.gyvacha.androidssh.R

sealed class BottomNavItem(
    @StringRes val label: Int,
    val icon: ImageVector
) {
    data object Settings : BottomNavItem(
        icon = Icons.Filled.Settings,
        label = R.string.label_settings
    )

    data object Hosts : BottomNavItem(
        icon = Icons.Filled.Dns,
        label = R.string.label_servers
    )

    data object Xray : BottomNavItem(
        icon = Icons.Filled.VpnKey,
        label = R.string.label_xray
    )

    companion object {
        fun getItemsList() = listOf(
            Settings to AppNavigation.SettingsRoute,
            Hosts to AppNavigation.HostsRoute,
            Xray to AppNavigation.XrayRoute
        )
    }
}