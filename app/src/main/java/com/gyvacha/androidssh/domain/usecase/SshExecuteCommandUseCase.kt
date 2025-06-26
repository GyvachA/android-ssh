package com.gyvacha.androidssh.domain.usecase

import com.gyvacha.androidssh.domain.repository.SshRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SshExecuteCommandUseCase @Inject constructor(
    private val repository: SshRepository
) {
    operator fun invoke(command: String): Flow<String> {
        return repository.executeCommand(command)
    }
}