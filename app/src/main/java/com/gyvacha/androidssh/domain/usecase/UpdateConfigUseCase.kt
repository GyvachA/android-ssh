package com.gyvacha.androidssh.domain.usecase

import com.gyvacha.androidssh.domain.model.ProxyConfig
import com.gyvacha.androidssh.domain.repository.ProxyConfigRepository

class UpdateConfigUseCase(
    private val repository: ProxyConfigRepository
) {
    suspend operator fun invoke(config: ProxyConfig) = repository.update(config)
}