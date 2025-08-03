package com.gyvacha.androidssh.domain.usecase

import com.gyvacha.androidssh.domain.model.ProxyConfig
import com.gyvacha.androidssh.domain.repository.ProxyConfigRepository

class SetActiveConfigUseCase(
    private val repository: ProxyConfigRepository
) {
    suspend operator fun invoke(config: ProxyConfig) = repository.setActiveConfig(config)
}