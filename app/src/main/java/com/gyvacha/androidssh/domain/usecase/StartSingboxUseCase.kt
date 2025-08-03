package com.gyvacha.androidssh.domain.usecase

import com.gyvacha.androidssh.domain.repository.SingboxRepository
import com.gyvacha.androidssh.utils.SingboxConfigFileManager

class StartSingboxUseCase(
    private val repository: SingboxRepository,
    private val fileManager: SingboxConfigFileManager
) {
    suspend operator fun invoke() {
        val configPath = fileManager.getFile().path
        repository.start(configPath)
    }
}