package com.gyvacha.androidssh.domain.model

import com.gyvacha.androidssh.data.local.entities.HostWithSshKeyEntity

data class HostWithSshKey(
    val host: Host,
    val sshKey: SshKey?
)

fun HostWithSshKeyEntity.toDomain() = HostWithSshKey(host.toDomain(), sshKey?.toDomain())
fun HostWithSshKey.toEntity() = HostWithSshKeyEntity(host.toEntity(), sshKey?.toEntity())
