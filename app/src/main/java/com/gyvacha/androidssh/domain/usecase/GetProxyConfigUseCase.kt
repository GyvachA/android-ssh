package com.gyvacha.androidssh.domain.usecase

import com.gyvacha.androidssh.domain.model.ProxyConfig
import com.gyvacha.androidssh.domain.repository.ProxyConfigRepository

class GetProxyConfigUseCase(
    private val repository: ProxyConfigRepository
) {
    suspend operator fun invoke(proxyConfigId: Long): ProxyConfig? {
        return repository.getProxyConfig(proxyConfigId)
    }
}
