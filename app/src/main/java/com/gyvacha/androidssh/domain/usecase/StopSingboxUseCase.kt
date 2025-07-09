package com.gyvacha.androidssh.domain.usecase

import com.gyvacha.androidssh.domain.repository.SingboxRepository

class StopSingboxUseCase(private val repository: SingboxRepository) {
    suspend operator fun invoke() {
        repository.stop()
    }
}