package com.gyvacha.androidssh.domain.usecase

import com.gyvacha.androidssh.domain.model.ProxyConfig
import com.gyvacha.androidssh.domain.repository.ProxyConfigRepository


class DeleteConfigUseCase(
    private val repository: ProxyConfigRepository
) {
    suspend operator fun invoke(config: ProxyConfig) = repository.delete(config)
}