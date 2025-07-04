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
        data class Terminal(
            val hostId: Int
        ) : AppNavigation

        @Serializable
        data object Hosts: AppNavigation

        @Serializable
        data class EditHost(
            val hostId: Int? = null
        ): AppNavigation
    }

    @Serializable
    data object XrayRoute : AppNavigation {
        @Serializable
        data object Xray: AppNavigation
    }
}
