package com.gyvacha.androidssh.domain.repository

import com.gyvacha.androidssh.domain.model.SshKey
import com.gyvacha.androidssh.utils.SshKeyGenerator
import kotlinx.coroutines.flow.Flow

interface SshKeyRepository {
    fun getSshKeys(): Flow<List<SshKey>>
    suspend fun insertSshKey(sshKey: SshKey): Long
    suspend fun deleteSshKey(sshKey: SshKey)
    suspend fun generateSshKey(algorithm: SshKeyGenerator.Algorithm, passphrase: String?): SshKey
}