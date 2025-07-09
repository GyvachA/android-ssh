package com.gyvacha.androidssh.domain.usecase

import com.gyvacha.androidssh.domain.repository.SingboxRepository
import kotlinx.coroutines.flow.Flow

class ObserveSingboxLogsUseCase(private val repository: SingboxRepository) {
    fun invoke(): Flow<String> {
        return repository.logs
    }
}