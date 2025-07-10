package com.gyvacha.androidssh.data.repository

import com.gyvacha.androidssh.data.local.dao.ProxyConfigDao
import com.gyvacha.androidssh.domain.model.ProxyConfig
import com.gyvacha.androidssh.domain.model.toEntity
import com.gyvacha.androidssh.domain.model.toModel
import com.gyvacha.androidssh.domain.repository.ProxyConfigRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class ProxyConfigRepositoryImpl(
    private val dao: ProxyConfigDao
) : ProxyConfigRepository {

    override suspend fun getActiveConfig(): ProxyConfig? {
        return withContext(Dispatchers.IO) {
            dao.getActive()?.toModel()
        }
    }

    override suspend fun insert(config: ProxyConfig) {
        return withContext(Dispatchers.IO) {
            dao.insert(config.toEntity())
        }
    }

    override suspend fun update(config: ProxyConfig) {
        return withContext(Dispatchers.IO) {
            dao.update(config.toEntity())
        }
    }

    override fun getConfigs(): Flow<List<ProxyConfig>> {
        return dao.getConfigs().map { entities ->
            entities.map { it.toModel() }
        }
    }

    override suspend fun delete(config: ProxyConfig) {
        return withContext(Dispatchers.IO) {
            dao.delete(config.toEntity())
        }
    }
}