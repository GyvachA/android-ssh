package com.gyvacha.androidssh.domain.usecase

import com.gyvacha.androidssh.domain.model.Host
import com.gyvacha.androidssh.domain.repository.HostRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetHostsUseCase @Inject constructor(
    private val repository: HostRepository
) {
    operator fun invoke(): Flow<List<Host>> = repository.getHosts()
}