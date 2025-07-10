package com.gyvacha.androidssh.domain.usecase

import com.gyvacha.androidssh.domain.model.ProxyConfig
import com.gyvacha.androidssh.domain.repository.ProxyConfigRepository
import kotlinx.coroutines.flow.Flow

class GetConfigsUseCase(
    private val repository: ProxyConfigRepository
) {
    operator fun invoke(): Flow<List<ProxyConfig>> = repository.getConfigs()
}