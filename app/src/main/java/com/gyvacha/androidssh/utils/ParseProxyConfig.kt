package com.gyvacha.androidssh.utils

import android.net.Uri
import com.gyvacha.androidssh.domain.model.ProxySpec
import androidx.core.net.toUri
import com.gyvacha.androidssh.domain.model.Transport

object ParseProxyConfig {

    fun parseProxyUri(uriString: String): ProxySpec? {
        val uri = uriString.toUri()
        val scheme = uri.scheme?.lowercase() ?: return null

        val server = uri.host ?: return null
        val port = uri.port.takeIf { it != -1 } ?: return null

        return when (scheme) {
            "vless" -> {
                val uuid = uri.userInfo ?: ""
                val flow = uri.getQueryParameter("flow")
                val encryption = uri.getQueryParameter("encryption") ?: "none"
                val transport = parseTransport(uri)
                ProxySpec.Vless(
                    server = server,
                    port = port,
                    uuid = uuid,
                    flow = flow,
                    encryption = encryption,
                    transport = transport
                )
            }
            "vmess" -> {
                val uuid = uri.userInfo ?: ""
                val alterId = uri.getQueryParameter("alterId")?.toIntOrNull() ?: 0
                val security = uri.getQueryParameter("security") ?: "auto"
                val transport = parseTransport(uri)
                ProxySpec.Vmess(
                    server = server,
                    port = port,
                    uuid = uuid,
                    alterId = alterId,
                    security = security,
                    transport = transport
                )
            }
            "trojan" -> {
                val password = if (uri.userInfo.isNullOrBlank()) uri.getQueryParameter("password") ?: "" else uri.userInfo
                val sni = uri.getQueryParameter("sni")
                ProxySpec.Trojan(
                    server = server,
                    port = port,
                    password = password ?: "",
                    sni = sni
                )
            }
            "shadowsocks" -> {
                val method = uri.getQueryParameter("method") ?: ""
                val password = if (uri.userInfo.isNullOrBlank()) uri.getQueryParameter("password") ?: "" else uri.userInfo
                ProxySpec.Shadowsocks(
                    server = server,
                    port = port,
                    method = method,
                    password = password ?: ""
                )
            }
            "socks" -> {
                val username = if (uri.userInfo.isNullOrBlank()) uri.getQueryParameter("username") else uri.userInfo
                val password = uri.getQueryParameter("password")
                ProxySpec.Socks(
                    server = server,
                    port = port,
                    username = username,
                    password = password
                )
            }
            "http" -> {
                val username = if (uri.userInfo.isNullOrBlank()) uri.getQueryParameter("username") else uri.userInfo
                val password = uri.getQueryParameter("password")
                ProxySpec.Http(
                    server = server,
                    port = port,
                    username = username,
                    password = password
                )
            }
            else -> null
        }
    }


    private fun parseTransport(uri: Uri): Transport {
        return when (uri.getQueryParameter("transport")?.lowercase()) {
            "ws" -> Transport.WS(
                path = uri.getQueryParameter("path") ?: "/",
                hostHeader = uri.getQueryParameter("hostHeader") ?: ""
            )
            "grpc" -> Transport.GRPC(
                serviceName = uri.getQueryParameter("serviceName") ?: "default"
            )
            else -> Transport.TCP
        }
    }
}