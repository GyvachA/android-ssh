package com.gyvacha.androidssh.domain.model

import com.gyvacha.androidssh.data.local.entities.SshKeyEntity

data class SshKey(
    val sshKeyId: Int = 0,
    val alias: String,
    val publicKey: String,
    val privateKey: String,
    val passphrase: String? = null
) {
    fun getPublicKeyPreview(): String {
        return "${publicKey.take(PUBLIC_KEY_REVIEW_LENGTH)}..."
    }

    companion object {
        private const val PUBLIC_KEY_REVIEW_LENGTH = 20
    }
}

fun SshKeyEntity.toDomain() = SshKey(sshKeyId, alias, publicKey, privateKey, passphrase)
fun SshKey.toEntity() = SshKeyEntity(sshKeyId, alias, publicKey, privateKey, passphrase)
