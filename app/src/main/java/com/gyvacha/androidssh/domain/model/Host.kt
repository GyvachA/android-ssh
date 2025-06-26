package com.gyvacha.androidssh.domain.model

import com.gyvacha.androidssh.data.local.entities.HostEntity
import kotlinx.serialization.Serializable

@Serializable
data class Host(
    val hostId: Int = 0,
    val alias: String,
    val hostNameOrIp: String,
    val port: Int,
    val userName: String,
    val password: String? = null,
    val sshKey: Int? = null
)

fun HostEntity.toDomain() = Host(hostId, alias, hostNameOrIp, port, userName, password, sshKey)
fun Host.toEntity() = HostEntity(hostId, alias, hostNameOrIp, port, userName, password, sshKey)
