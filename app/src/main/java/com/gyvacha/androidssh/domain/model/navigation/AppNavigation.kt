package com.gyvacha.androidssh.domain.model.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface AppNavigation : NavigationTarget {

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
        data object Hosts : AppNavigation

        @Serializable
        data class EditHost(
            val hostId: Int? = null
        ) : AppNavigation
    }

    @Serializable
    data object SingboxRoute : AppNavigation {
        @Serializable
        data object Singbox : AppNavigation

        @Serializable
        data object ImportFromQR : AppNavigation

        @Serializable
        data class EditProxyConfig(
            val proxyConfigId: Long? = null
        ) : AppNavigation
    }

    @Serializable
    data class EditSshKey(
        val sshKeyId: Int? = null
    ) : AppNavigation

    @Serializable
    data object GenerateSshKey
}
