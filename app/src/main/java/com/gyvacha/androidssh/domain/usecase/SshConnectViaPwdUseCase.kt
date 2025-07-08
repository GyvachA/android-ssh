package com.gyvacha.androidssh.domain.usecase

import com.gyvacha.androidssh.domain.repository.SshRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SshConnectViaPwdUseCase @Inject constructor(
    private val repository: SshRepository
) {
    suspend operator fun invoke(host: String, port: Int, username: String, password: String): Flow<String>? {
        return repository.connectViaPwd(host, port, username, password)
    }
}