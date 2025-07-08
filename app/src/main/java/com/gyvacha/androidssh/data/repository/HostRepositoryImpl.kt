package com.gyvacha.androidssh.data.repository

import com.gyvacha.androidssh.data.local.dao.HostDao
import com.gyvacha.androidssh.domain.model.Host
import com.gyvacha.androidssh.domain.model.HostWithSshKey
import com.gyvacha.androidssh.domain.model.toDomain
import com.gyvacha.androidssh.domain.model.toEntity
import com.gyvacha.androidssh.domain.repository.HostRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HostRepositoryImpl @Inject constructor(
    private val hostDao: HostDao
) : HostRepository {

    override fun getHosts(): Flow<List<Host>> = hostDao.getHosts().map { entities ->
        entities.map { it.toDomain() }
    }

    override suspend fun getHost(hostId: Int): Host {
        return withContext(Dispatchers.IO) {
            hostDao.getHost(hostId).toDomain()
        }
    }

    override suspend fun getHostWithSshKey(hostId: Int): HostWithSshKey {
        return withContext(Dispatchers.IO) {
            hostDao.getHostWithSshKey(hostId).toDomain()
        }
    }

    override suspend fun insertHost(host: Host) {
        withContext(Dispatchers.IO) {
            hostDao.insertHost(host.toEntity())
        }
    }

    override suspend fun deleteHost(host: Host) {
        withContext(Dispatchers.IO) {
            hostDao.deleteHost(host.toEntity())
        }
    }

    override suspend fun updateHost(host: Host) {
        withContext(Dispatchers.IO) {
            hostDao.updateHost(host.toEntity())
        }
    }
}