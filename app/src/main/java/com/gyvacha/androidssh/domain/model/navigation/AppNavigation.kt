package com.gyvacha.androidssh.domain.model.navigation

import kotlinx.serialization.Serializable


@Serializable
sealed interface AppNavigation: NavigationTarget {

    @Serializable
    data object SettingsRoute : AppNavigation {
        @Serializable
        data object Settings : AppNavigation
    }

    @Serializable
    data object HostsRoute : AppNavigation {
        @Serializable
        data object Hosts: AppNavigation

        @Serializable
        data object AddHost: AppNavigation
    }

    @Serializable
    data object XrayRoute : AppNavigation {
        @Serializable
        data object Xray: AppNavigation
    }
}
