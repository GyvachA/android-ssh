package com.gyvacha.androidssh.domain.usecase

import com.gyvacha.androidssh.domain.repository.SshRepository
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject

class SshConnectViaPwdUseCase @Inject constructor(
    private val repository: SshRepository
) {
    suspend operator fun invoke(
        host: String,
        port: Int,
        username: String,
        password: String,
        onHostKeyReceived: suspend (fingerprint: String) -> Boolean
    ): SharedFlow<String> {
        return repository.connectViaPwd(host, port, username, password, onHostKeyReceived)
    }
}
