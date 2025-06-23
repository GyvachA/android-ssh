package com.gyvacha.androidssh.domain.repository

import com.gyvacha.androidssh.domain.model.Host
import kotlinx.coroutines.flow.Flow

interface HostRepository {
    fun getHosts(): Flow<List<Host>>
    suspend fun insertHost(host: Host): Long
    suspend fun deleteHost(host: Host): Int
}