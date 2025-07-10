package com.gyvacha.androidssh.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class ProxySpec {
    @Serializable
    @SerialName("vless")
    data class Vless(
        val server: String,
        val port: Int,
        val uuid: String,
        val flow: String? = null,
        val encryption: String = "none",
        val transport: Transport = Transport.TCP
    ) : ProxySpec()

    @Serializable
    @SerialName("trojan")
    data class Trojan(
        val server: String,
        val port: Int,
        val password: String,
        val sni: String? = null
    ) : ProxySpec()

    @Serializable
    @SerialName("vmess")
    data class Vmess(
        val server: String,
        val port: Int,
        val uuid: String,
        val alterId: Int = 0,
        val security: String = "auto",
        val transport: Transport = Transport.TCP
    ) : ProxySpec()

    @Serializable
    @SerialName("shadowsocks")
    data class Shadowsocks(
        val server: String,
        val port: Int,
        val method: String,
        val password: String
    ) : ProxySpec()

    @Serializable
    @SerialName("socks")
    data class Socks(
        val server: String,
        val port: Int,
        val username: String? = null,
        val password: String? = null
    ) : ProxySpec()

    @Serializable
    @SerialName("http")
    data class Http(
        val server: String,
        val port: Int,
        val username: String? = null,
        val password: String? = null
    ) : ProxySpec()
}

@Serializable
sealed class Transport {
    @Serializable
    data object TCP : Transport()
    @Serializable
    data class WS(val path: String = "/", val hostHeader: String = "") : Transport()
    @Serializable
    data class GRPC(val serviceName: String = "default") : Transport()
}
