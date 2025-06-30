package com.gyvacha.androidssh.domain.usecase

import com.gyvacha.androidssh.domain.model.SshKey
import com.gyvacha.androidssh.domain.repository.SshKeyRepository
import com.gyvacha.androidssh.utils.SshKeyGenerator
import javax.inject.Inject

class GenerateSshKeyUseCase @Inject constructor(
    private val repository: SshKeyRepository
) {

    suspend operator fun invoke(algorithm: SshKeyGenerator.Algorithm, passphrase: String? = null): SshKey {
        return repository.generateSshKey(algorithm, passphrase)
    }
}