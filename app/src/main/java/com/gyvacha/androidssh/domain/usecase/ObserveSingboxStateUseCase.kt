package com.gyvacha.androidssh.domain.usecase

import com.gyvacha.androidssh.domain.repository.SingboxRepository
import kotlinx.coroutines.flow.StateFlow

class ObserveSingboxStateUseCase(private val repository: SingboxRepository) {
    fun invoke(): StateFlow<Boolean> {
        return repository.isRunning
    }
}