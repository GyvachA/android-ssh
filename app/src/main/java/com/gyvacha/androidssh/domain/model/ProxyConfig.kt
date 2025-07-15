package com.gyvacha.androidssh.domain.model

import com.gyvacha.androidssh.data.local.entities.ProxyConfigEntity

data class ProxyConfig(
    val id: Long,
    val alias: String,
    val type: ProxyType,
    val config: ProxySpec,
    val isActive: Boolean
)

fun ProxyConfig.getAddress(): String {
    return when (type) {
        ProxyType.VMESS -> {
            val cfg = config as? ProxySpec.Vmess
            if (cfg != null) "${cfg.server}:${cfg.port}" else ""
        }
        ProxyType.VLESS -> {
            val cfg = config as? ProxySpec.Vless
            if (cfg != null) "${cfg.server}:${cfg.port}" else ""
        }
        ProxyType.TROJAN -> {
            val cfg = config as? ProxySpec.Trojan
            if (cfg != null) "${cfg.server}:${cfg.port}" else ""
        }
        ProxyType.SHADOWSOCKS -> {
            val cfg = config as? ProxySpec.Shadowsocks
            if (cfg != null) "${cfg.server}:${cfg.port}" else ""
        }
        ProxyType.SOCKS -> {
            val cfg = config as? ProxySpec.Socks
            if (cfg != null) "${cfg.server}:${cfg.port}" else ""
        }
        ProxyType.HTTP -> {
            val cfg = config as? ProxySpec.Http
            if (cfg != null) "${cfg.server}:${cfg.port}" else ""
        }
    }
}

fun ProxyConfigEntity.toModel() = ProxyConfig(
    id = id,
    alias = alias,
    type = type,
    config = config,
    isActive = isActive
)

fun ProxyConfig.toEntity() = ProxyConfigEntity(
    id = id,
    alias = alias,
    type = type,
    config = config,
    isActive = isActive
)