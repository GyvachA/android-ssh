package com.gyvacha.androidssh.utils

import android.net.Uri
import androidx.core.net.toUri
import com.gyvacha.androidssh.domain.model.ProxySpec
import com.gyvacha.androidssh.domain.model.Transport

object ParseProxyConfig {

    fun parseProxyUri(uriString: String): Pair<String, ProxySpec>? {
        val uri = uriString.toUri()
        val scheme = uri.scheme?.lowercase() ?: return null

        val server = uri.host ?: return null
        val port = uri.port.takeIf { it != -1 } ?: return null
        val alias = uri.fragment ?: "Proxy Config"

        return when (scheme) {
            "vless" -> {
                val uuid = uri.userInfo ?: ""
                val flow = uri.getQueryParameter("flow")
                val transport = parseTransport(uri)
                val realityPublicKey = uri.getQueryParameter("pbk")
                val realityShortId = uri.getQueryParameter("sid")
                val realityFingerprint = uri.getQueryParameter("fp") ?: "chrome"
                val realityServerName = uri.getQueryParameter("sni")

                val proxySpec = ProxySpec.Vless(
                    server = server,
                    port = port,
                    uuid = uuid,
                    flow = flow,
                    transport = transport,
                    realityPublicKey = realityPublicKey,
                    realityShortId = realityShortId,
                    realityFingerprint = realityFingerprint,
                    realityServerName = realityServerName
                )
                Pair(alias, proxySpec)
            }

            "vmess" -> {
                val uuid = uri.userInfo ?: ""
                val alterId = uri.getQueryParameter("alterId")?.toIntOrNull() ?: 0
                val security = uri.getQueryParameter("security") ?: "auto"
                val transport = parseTransport(uri)
                val proxySpec = ProxySpec.Vmess(
                    server = server,
                    port = port,
                    uuid = uuid,
                    alterId = alterId,
                    security = security,
                    transport = transport
                )
                Pair(alias, proxySpec)
            }

            "trojan" -> {
                val password = if (uri.userInfo.isNullOrBlank()) {
                    uri.getQueryParameter("password")
                        ?: ""
                } else {
                    uri.userInfo
                }
                val sni = uri.getQueryParameter("sni")
                val proxySpec = ProxySpec.Trojan(
                    server = server,
                    port = port,
                    password = password ?: "",
                    sni = sni
                )
                Pair(alias, proxySpec)
            }

            "shadowsocks" -> {
                val method = uri.getQueryParameter("method") ?: ""
                val password = if (uri.userInfo.isNullOrBlank()) {
                    uri.getQueryParameter("password")
                        ?: ""
                } else {
                    uri.userInfo
                }
                val proxySpec = ProxySpec.Shadowsocks(
                    server = server,
                    port = port,
                    method = method,
                    password = password ?: ""
                )
                Pair(alias, proxySpec)
            }

            "socks" -> {
                val username =
                    if (uri.userInfo.isNullOrBlank()) uri.getQueryParameter("username") else uri.userInfo
                val password = uri.getQueryParameter("password")
                val proxySpec = ProxySpec.Socks(
                    server = server,
                    port = port,
                    username = username,
                    password = password
                )
                Pair(alias, proxySpec)
            }

            "http" -> {
                val username =
                    if (uri.userInfo.isNullOrBlank()) uri.getQueryParameter("username") else uri.userInfo
                val password = uri.getQueryParameter("password")
                val proxySpec = ProxySpec.Http(
                    server = server,
                    port = port,
                    username = username,
                    password = password
                )
                Pair(alias, proxySpec)
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
