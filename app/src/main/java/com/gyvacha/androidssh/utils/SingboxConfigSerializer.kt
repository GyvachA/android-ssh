package com.gyvacha.androidssh.utils

import com.gyvacha.androidssh.data.local.entities.ProxyConfigEntity
import com.gyvacha.androidssh.domain.model.ProxySpec
import com.gyvacha.androidssh.domain.model.Transport
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonObjectBuilder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

object SingboxConfigSerializer {

    fun serialize(config: ProxyConfigEntity): String {
        require(validate(config)) { "Invalid config: ${config.alias}" }
        val outbound = when (val spec = config.config) {
            is ProxySpec.Vless -> buildVless(spec)
            is ProxySpec.Vmess -> buildVmess(spec)
            is ProxySpec.Trojan -> buildTrojan(spec)
            is ProxySpec.Shadowsocks -> buildShadowsocks(spec)
            is ProxySpec.Socks -> buildSocks(spec)
            is ProxySpec.Http -> buildHttp(spec)
        }

        val json = buildJsonObject {
            put("log", buildJsonObject {
                put("disabled", false)
                put("level", "debug")
            })
            put("inbounds", JsonArray(listOf(buildTunInboundFd())))
            put("outbounds", JsonArray(listOf(outbound)))
        }

        return Json.encodeToString(JsonObject.serializer(), json)
    }

    private fun buildTunInboundFd(): JsonObject = buildJsonObject {
        put("type", "tun")
        put("tag", "tun-in")
        put("fd", "fd://0")
        put("mtu", 1500)
        put("auto_route", true)
        put("stack", "system")
        put("domain_strategy", "prefer_ipv4")
        put("address", JsonArray(listOf(
            JsonPrimitive("10.0.0.2/32"),
            JsonPrimitive("fd00::1/128")
        )))
        put("route_address", JsonArray(listOf(
            JsonPrimitive("0.0.0.0/0"),
            JsonPrimitive("::/0")
        )))
        put("route_exclude_address", JsonArray(listOf(
            JsonPrimitive("192.168.0.0/16"),
            JsonPrimitive("10.0.0.0/8"),
            JsonPrimitive("172.16.0.0/12"),
            JsonPrimitive("fd00::/8")
        )))
    }


    private fun buildVless(spec: ProxySpec.Vless): JsonObject = buildJsonObject {
        put("type", "vless")
        put("tag", "proxy")
        put("server", spec.server)
        put("server_port", spec.port)
        put("uuid", spec.uuid)
        spec.flow?.let { put("flow", it) }
        applyTransport(this, spec.transport)
    }

    private fun buildVmess(spec: ProxySpec.Vmess): JsonObject = buildJsonObject {
        put("type", "vmess")
        put("tag", "proxy")
        put("server", spec.server)
        put("server_port", spec.port)
        put("uuid", spec.uuid)
        put("alter_id", spec.alterId)
        put("security", spec.security)
        applyTransport(this, spec.transport)
    }

    private fun buildTrojan(spec: ProxySpec.Trojan): JsonObject = buildJsonObject {
        put("type", "trojan")
        put("tag", "proxy")
        put("server", spec.server)
        put("server_port", spec.port)
        put("password", spec.password)
        spec.sni?.let { put("sni", it) }
        applyTransport(this, Transport.TCP)
    }

    private fun buildShadowsocks(spec: ProxySpec.Shadowsocks): JsonObject = buildJsonObject {
        put("type", "shadowsocks")
        put("tag", "proxy")
        put("server", spec.server)
        put("server_port", spec.port)
        put("method", spec.method)
        put("password", spec.password)
        applyTransport(this, Transport.TCP)
    }

    private fun buildSocks(spec: ProxySpec.Socks): JsonObject = buildJsonObject {
        put("type", "socks")
        put("tag", "proxy")
        put("server", spec.server)
        put("server_port", spec.port)
        spec.username?.let { put("username", it) }
        spec.password?.let { put("password", it) }
        applyTransport(this, Transport.TCP)
    }

    private fun buildHttp(spec: ProxySpec.Http): JsonObject = buildJsonObject {
        put("type", "http")
        put("tag", "proxy")
        put("server", spec.server)
        put("server_port", spec.port)
        spec.username?.let { put("username", it) }
        spec.password?.let { put("password", it) }
        applyTransport(this, Transport.TCP)
    }

    private fun applyTransport(builder: JsonObjectBuilder, transport: Transport) {
        when (transport) {
            is Transport.TCP -> {
                builder.put("network", "tcp")
            }

            is Transport.WS -> {
                builder.put("network", "ws")
                builder.put("ws_settings", buildJsonObject {
                    put("path", transport.path)
                    if (transport.hostHeader.isNotBlank()) {
                        put("headers", buildJsonObject {
                            put("Host", transport.hostHeader)
                        })
                    }
                })
            }

            is Transport.GRPC -> {
                builder.put("network", "grpc")
                builder.put("grpc_settings", buildJsonObject {
                    put("service_name", transport.serviceName)
                })
            }
        }
    }

    private fun validate(config: ProxyConfigEntity): Boolean {
        return when (val spec = config.config) {
            is ProxySpec.Vless -> spec.server.isNotBlank() && spec.uuid.isNotBlank()
            is ProxySpec.Vmess -> spec.server.isNotBlank() && spec.uuid.isNotBlank()
            is ProxySpec.Trojan -> spec.server.isNotBlank() && spec.password.isNotBlank()
            is ProxySpec.Shadowsocks -> spec.method.isNotBlank() && spec.password.isNotBlank()
            is ProxySpec.Socks -> spec.server.isNotBlank()
            is ProxySpec.Http -> spec.server.isNotBlank()
        }
    }
}
