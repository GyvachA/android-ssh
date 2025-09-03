package com.gyvacha.androidssh.domain.usecase

import com.gyvacha.androidssh.domain.repository.SshRepository
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject

class SshConnectViaKeyUseCase @Inject constructor(
    private val repository: SshRepository
) {
    suspend operator fun invoke(
        host: String,
        port: Int,
        username: String,
        privateKey: String,
        publicKey: String,
        passphrase: String?
    ): SharedFlow<String> {
        return repository.connectViaKey(host, port, username, privateKey, publicKey, passphrase)
    }
}
