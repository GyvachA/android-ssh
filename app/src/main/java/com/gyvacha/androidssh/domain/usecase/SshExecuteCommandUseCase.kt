package com.gyvacha.androidssh.domain.usecase

import com.gyvacha.androidssh.domain.repository.SshRepository
import javax.inject.Inject

class SshExecuteCommandUseCase @Inject constructor(
    private val repository: SshRepository
) {
    suspend operator fun invoke(command: String) {
        return repository.executeCommand(command)
    }
}
