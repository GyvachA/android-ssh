package com.gyvacha.androidssh.data.repository

import com.gyvacha.androidssh.data.local.dao.HostDao
import com.gyvacha.androidssh.domain.model.Host
import com.gyvacha.androidssh.domain.model.toDomain
import com.gyvacha.androidssh.domain.model.toEntity
import com.gyvacha.androidssh.domain.repository.HostRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HostRepositoryImpl @Inject constructor(
    private val hostDao: HostDao
) : HostRepository {

    override fun getHosts(): Flow<List<Host>> = hostDao.getHosts().map { entities ->
        entities.map { it.toDomain() }
    }

    override suspend fun insertHost(host: Host) = hostDao.insertHost(host.toEntity())

    override suspend fun deleteHost(host: Host) = hostDao.deleteHost(host.toEntity())
}