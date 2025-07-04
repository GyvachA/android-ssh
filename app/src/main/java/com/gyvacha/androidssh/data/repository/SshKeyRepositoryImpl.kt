package com.gyvacha.androidssh.data.repository

import com.gyvacha.androidssh.data.local.dao.SshKeyDao
import com.gyvacha.androidssh.domain.model.SshKey
import com.gyvacha.androidssh.domain.model.toDomain
import com.gyvacha.androidssh.domain.model.toEntity
import com.gyvacha.androidssh.domain.repository.SshKeyRepository
import com.gyvacha.androidssh.utils.SshKeyGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SshKeyRepositoryImpl @Inject constructor(
    private val sshKeyDao: SshKeyDao
) : SshKeyRepository {
    override fun getSshKeys(): Flow<List<SshKey>> = sshKeyDao.getSshKeys().map { entities ->
        entities.map { it.toDomain() }
    }

    override suspend fun insertSshKey(sshKey: SshKey): Long {
        return withContext(Dispatchers.IO) {
            sshKeyDao.insertSshKey(sshKey.toEntity())
        }
    }

    override suspend fun deleteSshKey(sshKey: SshKey) {
        withContext(Dispatchers.IO) {
            sshKeyDao.deleteSshKey(sshKey.toEntity())
        }
    }

    override suspend fun generateSshKey(algorithm: SshKeyGenerator.Algorithm, passphrase: String?): SshKey {
        return withContext(Dispatchers.IO) {
            val keyPair = when (algorithm) {
                SshKeyGenerator.Algorithm.ALGORITHM_RSA -> {
                    SshKeyGenerator.generateRsaKeyPair()
                }
                SshKeyGenerator.Algorithm.ALGORITHM_ED25519 -> {
                    SshKeyGenerator.generateEd25519KeyPair()
                }
            }
            SshKey(
                alias = "Generated key pair",
                publicKey = SshKeyGenerator.convertToOpenSshPublicKey(keyPair),
                privateKey = SshKeyGenerator.privateKeyPem(keyPair, passphrase)
            )
        }
    }
}