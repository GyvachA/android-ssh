package com.gyvacha.androidssh.domain.usecase

import com.gyvacha.androidssh.domain.model.ProxyConfig
import com.gyvacha.androidssh.domain.repository.ProxyConfigRepository

class GetActiveConfigUseCase(
    private val repository: ProxyConfigRepository
) {
    suspend operator fun invoke(): ProxyConfig? = repository.getActiveConfig()
}