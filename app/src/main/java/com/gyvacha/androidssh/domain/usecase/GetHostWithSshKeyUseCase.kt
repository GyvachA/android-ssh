package com.gyvacha.androidssh.domain.usecase

import com.gyvacha.androidssh.domain.model.HostWithSshKey
import com.gyvacha.androidssh.domain.repository.HostRepository
import javax.inject.Inject

class GetHostWithSshKeyUseCase @Inject constructor(
    private val repository: HostRepository
) {
    suspend operator fun invoke(hostId: Int): HostWithSshKey = repository.getHostWithSshKey(hostId)
}