package com.gyvacha.androidssh.domain.usecase

import com.gyvacha.androidssh.domain.model.toEntity
import com.gyvacha.androidssh.domain.repository.ProxyConfigRepository
import com.gyvacha.androidssh.utils.SingboxConfigFileManager
import com.gyvacha.androidssh.utils.SingboxConfigSerializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class GenerateSingboxConfigFileUseCase(
    private val repository: ProxyConfigRepository,
    private val fileManager: SingboxConfigFileManager
) {
    suspend operator fun invoke(): File {
        return withContext(Dispatchers.IO) {
            val config = repository.getActiveConfig() ?: throw IllegalStateException("No active config")
            val json = SingboxConfigSerializer.serialize(config.toEntity())
            fileManager.writeToFile(json)
        }
    }
}