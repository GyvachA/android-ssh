package com.gyvacha.androidssh.domain.usecase

import com.gyvacha.androidssh.domain.model.SshKey
import com.gyvacha.androidssh.domain.repository.SshKeyRepository
import javax.inject.Inject

class InsertSshKeyUseCase @Inject constructor(
    private val repository: SshKeyRepository
) {
    suspend operator fun invoke(sshKey: SshKey) = repository.insertSshKey(sshKey)
}