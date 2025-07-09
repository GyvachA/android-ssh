package com.gyvacha.androidssh.domain.usecase

import com.gyvacha.androidssh.domain.repository.SingboxRepository

class StartSingboxUseCase(private val repository: SingboxRepository) {
    suspend operator fun invoke(configPath: String) {
        repository.start(configPath)
    }
}