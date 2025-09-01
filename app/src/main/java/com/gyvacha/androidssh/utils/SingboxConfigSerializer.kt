package com.gyvacha.androidssh.utils

import com.gyvacha.androidssh.data.local.entities.ProxyConfigEntity
import com.gyvacha.androidssh.domain.model.ProxySpec
import com.gyvacha.androidssh.domain.model.Transport
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

object SingboxConfigSerializer {

    fun serialize(config: ProxyConfigEntity): String {
        val singboxConfig = SingboxConfig(
            log = LogConfig(),
            inbounds = listOf(TunInbound()),
            outbounds = listOf(
                buildOutbound(config.config),
                Outbound(type = "direct", tag = "direct"),
                Outbound(type = "block", tag = "block"),
                Outbound(type = "dns", tag = "dns-out"),
            ),
            route = RouteConfig(
                rules = listOf(
                    RouteRule(protocol = "dns", outbound = "dns-out"),
                    RouteRule(ipIsPrivate = true, outbound = "direct"),
                    RouteRule(network = "tcp,udp", outbound = "proxy")
                ),
                final = "proxy"
            ),
            dns = DnsConfig(
                servers = listOf(
                    DnsServer(
                        tag = "system",
                        address = "local",
                        detour = "direct",
                    ),
                    DnsServer(
                        tag = "google-doh",
                        address = "https://dns.google/dns-query",
                        detour = "direct",
                        addressResolver = "system"
                    ),
                    DnsServer(
                        tag = "cloudflare-doh",
                        address = "https://cloudflare-dns.com/dns-query",
                        detour = "direct",
                        addressResolver = "system"
                    )
                ),
                rules = listOf(
                    DnsRule(domain = listOf("geosite:private"), server = "direct"),
                    DnsRule(domainSuffix = listOf(".lan", ".local"), server = "direct")
                ),
                final = "google-doh",
                strategy = "prefer_ipv4"
            )
        )

        val json = Json {
            encodeDefaults = true
            explicitNulls = false
            prettyPrint = true
        }

        val jsonString = json.encodeToString(SingboxConfig.serializer(), singboxConfig)
        return jsonString
    }

    private fun buildOutbound(spec: ProxySpec): Outbound {
        return when (spec) {
            is ProxySpec.Vless -> {
                val transportConfig = when (spec.transport) {
                    is Transport.WS -> TransportConfig(
                        type = "ws",
                        path = spec.transport.path,
                        headers = mapOf("Host" to spec.transport.hostHeader).takeIf {
                            spec.transport.hostHeader.isNotBlank()
                        }
                    )
                    is Transport.GRPC -> TransportConfig(
                        type = "grpc",
                        serviceName = spec.transport.serviceName
                    )
                    else -> null
                }

                if (spec.realityPublicKey != null && spec.realityShortId != null) {
                    Outbound(
                        type = "vless",
                        tag = "proxy",
                        server = spec.server,
                        serverPort = spec.port,
                        uuid = spec.uuid,
                        flow = spec.flow,
                        transport = transportConfig,
                        tls = TlsConfig(
                            enabled = true,
                            serverName = spec.realityServerName ?: spec.server,
                            insecure = false,
                            reality = RealityConfig(
                                enabled = true,
                                publicKey = spec.realityPublicKey,
                                shortId = spec.realityShortId
                            ),
                            utls = UtlsConfig(
                                enabled = true,
                                fingerprint = spec.realityFingerprint ?: "chrome"
                            )
                        )
                    )
                } else {
                    Outbound(
                        type = "vless",
                        tag = "proxy",
                        server = spec.server,
                        serverPort = spec.port,
                        uuid = spec.uuid,
                        flow = spec.flow,
                        transport = transportConfig,
                        tls = if (spec.port == 443) {
                            TlsConfig(
                                enabled = true,
                                serverName = spec.realityServerName ?: spec.server,
                                insecure = false,
                                alpn = listOf("h2", "http/1.1")
                            )
                        } else {
                            null
                        }
                    )
                }
            }

            is ProxySpec.Vmess -> {
                val transportConfig = when (spec.transport) {
                    is Transport.WS -> TransportConfig(
                        type = "ws",
                        path = spec.transport.path,
                        headers = mapOf("Host" to spec.transport.hostHeader).takeIf {
                            spec.transport.hostHeader.isNotBlank()
                        }
                    )
                    is Transport.GRPC -> TransportConfig(
                        type = "grpc",
                        serviceName = spec.transport.serviceName
                    )
                    else -> null
                }

                Outbound(
                    type = "vmess",
                    tag = "proxy",
                    server = spec.server,
                    serverPort = spec.port,
                    uuid = spec.uuid,
                    alterId = spec.alterId,
                    transport = transportConfig,
                    tls = if (spec.port == 443 || spec.sni != null) {
                        TlsConfig(
                            enabled = true,
                            serverName = spec.sni ?: spec.server,
                            insecure = false,
                            alpn = listOf("h2", "http/1.1")
                        )
                    } else {
                        null
                    }
                )
            }

            is ProxySpec.Trojan -> Outbound(
                type = "trojan",
                tag = "proxy",
                server = spec.server,
                serverPort = spec.port,
                password = spec.password,
                tls = TlsConfig(
                    enabled = true,
                    serverName = spec.sni ?: spec.server,
                    insecure = false,
                    alpn = spec.alpn ?: listOf("h2", "http/1.1")
                )
            )

            is ProxySpec.Shadowsocks -> Outbound(
                type = "shadowsocks",
                tag = "proxy",
                server = spec.server,
                serverPort = spec.port,
                method = spec.method,
                password = spec.password,
            )

            is ProxySpec.Socks -> Outbound(
                type = "socks",
                tag = "proxy",
                server = spec.server,
                serverPort = spec.port,
                version = spec.version.toString(),
                username = spec.username,
                password = spec.password
            )

            is ProxySpec.Http -> Outbound(
                type = "http",
                tag = "proxy",
                server = spec.server,
                serverPort = spec.port,
                username = spec.username,
                password = spec.password,
                tls = if (spec.https || spec.port == 443) {
                    TlsConfig(
                        enabled = true,
                        serverName = spec.server,
                        insecure = false
                    )
                } else {
                    null
                }
            )
        }
    }
}

@Serializable
data class TlsConfig(
    val enabled: Boolean,
    @SerialName("server_name") val serverName: String,
    val insecure: Boolean = false,
    val alpn: List<String>? = null,
    val reality: RealityConfig? = null,
    val utls: UtlsConfig? = null
)

@Serializable
data class RealityConfig(
    val enabled: Boolean,
    @SerialName("public_key") val publicKey: String,
    @SerialName("short_id") val shortId: String
)

@Serializable
data class UtlsConfig(
    val enabled: Boolean,
    val fingerprint: String
)

@Serializable
data class DnsRule(
    val domain: List<String>? = null,
    @SerialName("domain_suffix") val domainSuffix: List<String>? = null,
    @SerialName("domain_keyword") val domainKeyword: List<String>? = null,
    @SerialName("domain_regex") val domainRegex: List<String>? = null,
    val server: String
)

@Serializable
data class Outbound(
    val type: String,
    val tag: String,
    val server: String? = null,
    @SerialName("server_port") val serverPort: Int? = null,
    val uuid: String? = null,
    val flow: String? = null,
    val password: String? = null,
    val method: String? = null,
    val username: String? = null,
    val version: String? = null,
    @SerialName("alter_id") val alterId: Int? = null,
    val transport: TransportConfig? = null,
    val tls: TlsConfig? = null
)

@Serializable
data class TunInbound(
    val type: String = "tun",
    val tag: String = "tun-in",
    @SerialName("interface_name") val interfaceName: String = "tun0",
    @SerialName("inet4_address") val inet4Address: List<String> = listOf("172.19.0.1/28"),
    @SerialName("inet6_address") val inet6Address: List<String> = listOf("fd00::1/126"),
    val mtu: Int = 1400,
    @SerialName("auto_route") val autoRoute: Boolean = true,
    val sniff: Boolean = true,
    @SerialName("sniff_override_destination") val sniffOverrideDestination: Boolean = true,
    @SerialName("domain_strategy") val domainStrategy: String = "prefer_ipv4",
    val stack: String = "gvisor"
)

@Serializable
data class RouteRule(
    val protocol: String? = null,
    @SerialName("ip_is_private") val ipIsPrivate: Boolean? = null,
    val domain: List<String>? = null,
    val port: Int? = null,
    @SerialName("ip_cidr") val ipCidr: List<String>? = null,
    val network: String? = null,
    val outbound: String
)

@Serializable
data class RouteConfig(
    val rules: List<RouteRule>,
    val final: String
)

@Serializable
data class DnsServer(
    val tag: String,
    val address: String,
    val detour: String,
    @SerialName("server_name") val serverName: String? = null,
    @SerialName("address_resolver") val addressResolver: String? = null
)

@Serializable
data class DnsConfig(
    val servers: List<DnsServer>,
    val rules: List<DnsRule>? = null,
    val final: String,
    val strategy: String? = null
)

@Serializable
data class LogConfig(
    val disabled: Boolean = false,
    val level: String = "trace"
)

@Serializable
data class SingboxConfig(
    val log: LogConfig,
    val inbounds: List<TunInbound>,
    val outbounds: List<Outbound>,
    val route: RouteConfig,
    val dns: DnsConfig? = null
)

@Serializable
data class TransportConfig(
    val type: String,
    val path: String? = null,
    val headers: Map<String, String>? = null,
    @SerialName("service_name") val serviceName: String? = null
)
