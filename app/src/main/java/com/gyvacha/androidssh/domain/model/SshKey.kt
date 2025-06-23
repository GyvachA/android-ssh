package com.gyvacha.androidssh.domain.model

import com.gyvacha.androidssh.data.local.entities.SshKeyEntity

data class SshKey(
    val sshKeyId: Int = 0,
    val publicKey: String,
    val privateKey: String?
)

fun SshKeyEntity.toDomain() = SshKey(sshKeyId, publicKey, privateKey)
fun SshKey.toEntity() = SshKeyEntity(sshKeyId, publicKey, privateKey)
