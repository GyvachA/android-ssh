package com.gyvacha.androidssh.domain.usecase

import com.gyvacha.androidssh.domain.model.SshKey
import com.gyvacha.androidssh.domain.repository.SshKeyRepository
import javax.inject.Inject

class DeleteSshKeyUseCase @Inject constructor(
    private val repository: SshKeyRepository
) {
    suspend operator fun invoke(sshKey: SshKey) {
        repository.deleteSshKey(sshKey)
    }
}