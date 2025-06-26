package com.gyvacha.androidssh.domain.usecase

import com.gyvacha.androidssh.domain.model.Host
import com.gyvacha.androidssh.domain.repository.HostRepository
import javax.inject.Inject

class GetHostUseCase @Inject constructor(
    private val repository: HostRepository
) {
    suspend operator fun invoke(hostId: Int): Host = repository.getHost(hostId)
}