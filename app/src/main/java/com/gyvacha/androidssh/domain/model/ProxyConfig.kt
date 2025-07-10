package com.gyvacha.androidssh.domain.model

import com.gyvacha.androidssh.data.local.entities.ProxyConfigEntity

data class ProxyConfig(
    val id: Long,
    val alias: String,
    val type: ProxyType,
    val config: ProxySpec,
    val isActive: Boolean
)

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