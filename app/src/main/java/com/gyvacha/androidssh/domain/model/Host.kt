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
    val authType: SshAuthType,
    val password: String? = null,
    val sshKey: Int? = null
)

fun HostEntity.toDomain() = Host(
    hostId = hostId,
    alias = alias,
    hostNameOrIp = hostNameOrIp,
    port = port,
    userName = userName,
    password = password,
    sshKey = sshKey,
    authType = SshAuthType.valueOf(authType)
)
fun Host.toEntity() = HostEntity(
    hostId = hostId,
    alias =alias,
    hostNameOrIp = hostNameOrIp,
    port = port,
    userName = userName,
    password = password,
    sshKey = sshKey,
    authType = authType.name
)


enum class SshAuthType {
    PASSWORD,
    SSH_KEY
}
