package com.gyvacha.androidssh.domain.usecase

import com.gyvacha.androidssh.domain.model.Status
import com.gyvacha.androidssh.domain.repository.SingboxRepository
import kotlinx.coroutines.flow.StateFlow

class ObserveSingboxStateUseCase(private val repository: SingboxRepository) {
    operator fun invoke(): StateFlow<Status> {
        return repository.serviceStatus
    }
}