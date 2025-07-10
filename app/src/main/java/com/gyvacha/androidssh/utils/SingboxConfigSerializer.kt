package com.gyvacha.androidssh.utils

import com.gyvacha.androidssh.data.local.entities.ProxyConfigEntity
import com.gyvacha.androidssh.domain.model.ProxySpec
import com.gyvacha.androidssh.domain.model.Transport
import kotlinx.serialization.json.*

object SingboxConfigSerializer {

    fun serialize(config: ProxyConfigEntity): String {
        require(validate(config)) { "Invalid config: ${config.alias}" }

        val spec = config.config

        val outbound = when (spec) {
            is ProxySpec.Vless -> buildVless(spec)
            is ProxySpec.Vmess -> buildVmess(spec)
            is ProxySpec.Trojan -> buildTrojan(spec)
            is ProxySpec.Shadowsocks -> buildShadowsocks(spec)
            is ProxySpec.Socks -> buildSocks(spec)
            is ProxySpec.Http -> buildHttp(spec)
        }

        val json = buildJsonObject {
            put("log", buildJsonObject { put("disabled", true) })
            put("outbounds", JsonArray(listOf(outbound)))
        }

        return Json.encodeToString(JsonObject.serializer(), json)
    }

    private fun buildVless(spec: ProxySpec.Vless): JsonObject = buildJsonObject {
        put("type", "vless")
        put("tag", "proxy")
        put("server", spec.server)
        put("server_port", spec.port)
        put("uuid", spec.uuid)
        put("encryption", spec.encryption)
        spec.flow?.let { put("flow", it) }
        put("transport", buildTransport(spec.transport))
    }

    private fun buildVmess(spec: ProxySpec.Vmess): JsonObject = buildJsonObject {
        put("type", "vmess")
        put("tag", "proxy")
        put("server", spec.server)
        put("server_port", spec.port)
        put("uuid", spec.uuid)
        put("alter_id", spec.alterId)
        put("security", spec.security)
        put("transport", buildTransport(spec.transport))
    }

    private fun buildTrojan(spec: ProxySpec.Trojan): JsonObject = buildJsonObject {
        put("type", "trojan")
        put("tag", "proxy")
        put("server", spec.server)
        put("server_port", spec.port)
        put("password", spec.password)
        spec.sni?.let { put("sni", it) }
        put("transport", buildTransport(Transport.TCP))
    }

    private fun buildShadowsocks(spec: ProxySpec.Shadowsocks): JsonObject = buildJsonObject {
        put("type", "shadowsocks")
        put("tag", "proxy")
        put("server", spec.server)
        put("server_port", spec.port)
        put("method", spec.method)
        put("password", spec.password)
        put("transport", buildTransport(Transport.TCP))
    }

    private fun buildSocks(spec: ProxySpec.Socks): JsonObject = buildJsonObject {
        put("type", "socks")
        put("tag", "proxy")
        put("server", spec.server)
        put("server_port", spec.port)
        spec.username?.let { put("username", it) }
        spec.password?.let { put("password", it) }
        put("transport", buildTransport(Transport.TCP))
    }

    private fun buildHttp(spec: ProxySpec.Http): JsonObject = buildJsonObject {
        put("type", "http")
        put("tag", "proxy")
        put("server", spec.server)
        put("server_port", spec.port)
        spec.username?.let { put("username", it) }
        spec.password?.let { put("password", it) }
        put("transport", buildTransport(Transport.TCP))
    }

    private fun buildTransport(transport: Transport): JsonObject = when (transport) {
        is Transport.TCP -> buildJsonObject { put("type", "tcp") }
        is Transport.WS -> buildJsonObject {
            put("type", "ws")
            put("wsSettings", buildJsonObject {
                put("path", transport.path)
                if (transport.hostHeader.isNotEmpty()) put(
                    "headers",
                    buildJsonObject { put("Host", transport.hostHeader) })
            })
        }

        is Transport.GRPC -> buildJsonObject {
            put("type", "grpc")
            put("grpcSettings", buildJsonObject {
                put("serviceName", transport.serviceName)
            })
        }
    }

    fun validate(config: ProxyConfigEntity): Boolean {
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
