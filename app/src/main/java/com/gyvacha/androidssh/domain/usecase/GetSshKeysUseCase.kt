package com.gyvacha.androidssh.domain.usecase

import com.gyvacha.androidssh.domain.model.SshKey
import com.gyvacha.androidssh.domain.repository.SshKeyRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSshKeysUseCase @Inject constructor(
    private val repository: SshKeyRepository
) {
    operator fun invoke(): Flow<List<SshKey>> = repository.getSshKeys()
}