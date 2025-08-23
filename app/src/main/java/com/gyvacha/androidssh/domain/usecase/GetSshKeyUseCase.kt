package com.gyvacha.androidssh.domain.usecase

import com.gyvacha.androidssh.domain.model.SshKey
import com.gyvacha.androidssh.domain.repository.SshKeyRepository
import javax.inject.Inject

class GetSshKeyUseCase @Inject constructor(
    private val repository: SshKeyRepository
) {
    suspend operator fun invoke(sshKeyId: Int): SshKey {
        return repository.getSshKey(sshKeyId)
    }
}
