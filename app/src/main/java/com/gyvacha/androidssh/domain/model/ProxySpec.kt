package com.gyvacha.androidssh.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class ProxySpec {
    abstract val server: String
    abstract val port: Int

    @Serializable
    @SerialName("vless")
    data class Vless(
        override val server: String,
        override val port: Int,
        val uuid: String,
        val encryption: String = "none",
        val flow: String? = null,
        val transport: Transport = Transport.TCP,
        val realityPublicKey: String? = null,
        val realityShortId: String? = null,
        val realityFingerprint: String? = null,
        val realityServerName: String? = null
    ) : ProxySpec()

    @Serializable
    @SerialName("trojan")
    data class Trojan(
        override val server: String,
        override val port: Int,
        val password: String?,
        val sni: String? = null,
        val alpn: List<String>? = null,
        val security: String? = null
    ) : ProxySpec()

    @Serializable
    @SerialName("vmess")
    data class Vmess(
        override val server: String,
        override val port: Int,
        val uuid: String,
        val alterId: Int = 0,
        val security: String = "auto",
        val transport: Transport = Transport.TCP,
        val tls: Boolean = false,
        val sni: String? = null,
        val alpn: List<String>? = null,
        val name: String? = null
    ) : ProxySpec()

    @Serializable
    @SerialName("shadowsocks")
    data class Shadowsocks(
        override val server: String,
        override val port: Int,
        val method: String,
        val password: String?,
        val plugin: String? = null
    ) : ProxySpec()

    @Serializable
    @SerialName("socks")
    data class Socks(
        override val server: String,
        override val port: Int,
        val username: String? = null,
        val password: String? = null,
        val version: Int = 5
    ) : ProxySpec()

    @Serializable
    @SerialName("http")
    data class Http(
        override val server: String,
        override val port: Int,
        val username: String? = null,
        val password: String? = null,
        val https: Boolean = false
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

fun ProxySpec.withServer(newServer: String): ProxySpec = when (this) {
    is ProxySpec.Vless -> copy(server = newServer)
    is ProxySpec.Vmess -> copy(server = newServer)
    is ProxySpec.Trojan -> copy(server = newServer)
    is ProxySpec.Shadowsocks -> copy(server = newServer)
    is ProxySpec.Socks -> copy(server = newServer)
    is ProxySpec.Http -> copy(server = newServer)
}

fun ProxySpec.withPort(newPort: Int): ProxySpec = when (this) {
    is ProxySpec.Vless -> copy(port = newPort)
    is ProxySpec.Vmess -> copy(port = newPort)
    is ProxySpec.Trojan -> copy(port = newPort)
    is ProxySpec.Shadowsocks -> copy(port = newPort)
    is ProxySpec.Socks -> copy(port = newPort)
    is ProxySpec.Http -> copy(port = newPort)
}
