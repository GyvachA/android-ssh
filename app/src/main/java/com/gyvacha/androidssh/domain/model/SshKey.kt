package com.gyvacha.androidssh.domain.model

import com.gyvacha.androidssh.data.local.entities.SshKeyEntity

data class SshKey(
    val sshKeyId: Int = 0,
    val alias: String,
    val publicKey: String,
    val privateKey: String? = null,
    val passphrase: String? = null
)

fun SshKeyEntity.toDomain() = SshKey(sshKeyId, alias, publicKey, privateKey, passphrase)
fun SshKey.toEntity() = SshKeyEntity(sshKeyId, alias, publicKey, privateKey, passphrase)
