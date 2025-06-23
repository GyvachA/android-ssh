package com.gyvacha.androidssh.data.repository

import com.gyvacha.androidssh.data.local.dao.SshKeyDao
import com.gyvacha.androidssh.domain.model.SshKey
import com.gyvacha.androidssh.domain.model.toDomain
import com.gyvacha.androidssh.domain.model.toEntity
import com.gyvacha.androidssh.domain.repository.SshKeyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SshKeyRepositoryImpl @Inject constructor(
    private val sshKeyDao: SshKeyDao
) : SshKeyRepository {
    override fun getSshKeys(): Flow<List<SshKey>> = sshKeyDao.getSshKeys().map { entities ->
        entities.map { it.toDomain() }
    }

    override suspend fun insertSshKey(sshKey: SshKey) = sshKeyDao.insertSshKey(sshKey.toEntity())

    override suspend fun deleteSshKey(sshKey: SshKey) = sshKeyDao.deleteSshKey(sshKey.toEntity())
}