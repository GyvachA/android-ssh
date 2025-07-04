package com.gyvacha.androidssh.domain.repository

import com.gyvacha.androidssh.domain.model.Host
import com.gyvacha.androidssh.domain.model.HostWithSshKey
import kotlinx.coroutines.flow.Flow

interface HostRepository {
    fun getHosts(): Flow<List<Host>>
    suspend fun getHost(hostId: Int): Host
    suspend fun getHostWithSshKey(hostId: Int): HostWithSshKey
    suspend fun insertHost(host: Host)
    suspend fun deleteHost(host: Host)
}