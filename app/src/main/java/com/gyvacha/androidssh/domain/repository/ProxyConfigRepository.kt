package com.gyvacha.androidssh.domain.repository

import com.gyvacha.androidssh.domain.model.ProxyConfig
import kotlinx.coroutines.flow.Flow

interface ProxyConfigRepository {
    suspend fun getActiveConfig(): ProxyConfig?
    suspend fun insert(config: ProxyConfig)
    suspend fun update(config: ProxyConfig)
    fun getConfigs(): Flow<List<ProxyConfig>>
    suspend fun delete(config: ProxyConfig)
}