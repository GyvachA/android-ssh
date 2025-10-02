package com.gyvacha.androidssh.utils

import android.net.Uri
import android.util.Base64
import androidx.core.net.toUri
import com.gyvacha.androidssh.domain.model.ProxySpec
import com.gyvacha.androidssh.domain.model.Transport
import org.json.JSONObject

object ParseProxyConfig {

    fun parseProxyUri(uriString: String): Pair<String, ProxySpec>? {
        val scheme = uriString.substringBefore("://").lowercase()

        return when (scheme) {
            "vmess" -> parseVmess(uriString)
            "ss", "shadowsocks" -> parseShadowsocks(uriString)
            else -> parseUriBasedProxy(uriString)
        }
    }

    private fun parseShadowsocks(uriString: String): Pair<String, ProxySpec.Shadowsocks>? {
        val alias = uriString.substringAfterLast("#", "Proxy Config")

        val raw = uriString.removePrefix("ss://")
            .removePrefix("shadowsocks://")
            .substringBefore("#")
            .substringBefore("?")

        val (credPart, serverPart) = raw.split("@", limit = 2)
        val decoded = runCatching {
            String(Base64.decode(credPart, Base64.NO_PADDING or Base64.NO_WRAP or Base64.URL_SAFE))
        }.getOrNull() ?: return null

        val (method, password) = decoded.split(":", limit = 2)
        val (server, portStr) = serverPart.split(":", limit = 2)

        val proxySpec = ProxySpec.Shadowsocks(
            server = server,
            port = portStr.toIntOrNull() ?: return null,
            method = method,
            password = password
        )
        return alias to proxySpec
    }

    private fun parseVmess(uriString: String): Pair<String, ProxySpec.Vmess>? {
        val base64 = uriString.removePrefix("vmess://")
        val decoded = runCatching {
            String(Base64.decode(base64, Base64.NO_PADDING or Base64.NO_WRAP or Base64.URL_SAFE))
        }.getOrNull() ?: return null

        return runCatching {
            val json = JSONObject(decoded)

            val server = json.optString("add", "")
            val port = json.optInt("port", 0)
            val uuid = json.optString("id", "")
            val alterId = json.optInt("aid", 0)
            val security = json.optString("scy", "auto")
            val alias = json.optString("ps", "Proxy Config")

            val transport = when (json.optString("net")) {
                "ws" -> Transport.WS(
                    path = json.optString("path", "/"),
                    hostHeader = json.optString("host", "")
                )
                "grpc" -> Transport.GRPC(
                    serviceName = json.optString("serviceName", "default")
                )
                else -> Transport.TCP
            }

            val tlsEnabled = json.optString("tls", "none") != "none"
            val sni = json.optString("sni", null)
            val alpn = json.optJSONArray("alpn")?.let { arr ->
                List(arr.length()) { i -> arr.optString(i) }
            }

            val proxySpec = ProxySpec.Vmess(
                server = server,
                port = port,
                uuid = uuid,
                alterId = alterId,
                security = security,
                transport = transport,
                tls = tlsEnabled,
                sni = sni,
                alpn = alpn
            )

            alias to proxySpec
        }.getOrNull()
    }

    private fun parseUriBasedProxy(uriString: String): Pair<String, ProxySpec>? {
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
                val proxySpec = ProxySpec.Vless(
                    server = server,
                    port = port,
                    uuid = uuid,
                    flow = flow,
                    transport = transport,
                    realityPublicKey = uri.getQueryParameter("pbk"),
                    realityShortId = uri.getQueryParameter("sid"),
                    realityFingerprint = uri.getQueryParameter("fp") ?: "chrome",
                    realityServerName = uri.getQueryParameter("sni")
                )
                alias to proxySpec
            }

            "trojan" -> {
                val password = if (uri.userInfo.isNullOrBlank()) {
                    uri.getQueryParameter("password") ?: ""
                } else {
                    uri.userInfo
                }
                val security = uri.getQueryParameter("security")
                val proxySpec = ProxySpec.Trojan(
                    server = server,
                    port = port,
                    password = password,
                    sni = uri.getQueryParameter("sni"),
                    security = security
                )
                alias to proxySpec
            }

            "socks" -> {
                val proxySpec = ProxySpec.Socks(
                    server = server,
                    port = port,
                    username = uri.userInfo ?: uri.getQueryParameter("username"),
                    password = uri.getQueryParameter("password")
                )
                alias to proxySpec
            }

            "http" -> {
                val proxySpec = ProxySpec.Http(
                    server = server,
                    port = port,
                    username = uri.userInfo ?: uri.getQueryParameter("username"),
                    password = uri.getQueryParameter("password")
                )
                alias to proxySpec
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
