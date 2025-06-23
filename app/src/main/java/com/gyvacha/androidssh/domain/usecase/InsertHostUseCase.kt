package com.gyvacha.androidssh.domain.usecase

import com.gyvacha.androidssh.domain.model.Host
import com.gyvacha.androidssh.domain.repository.HostRepository
import javax.inject.Inject

class InsertHostUseCase @Inject constructor(
    private val repository: HostRepository
) {
    suspend operator fun invoke(host: Host) = repository.insertHost(host)
}