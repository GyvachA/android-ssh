package com.gyvacha.androidssh.domain.usecase

import com.gyvacha.androidssh.domain.repository.SshRepository
import javax.inject.Inject

class SshConnectViaKeyUseCase @Inject constructor(
    private val repository: SshRepository
) {
    suspend operator fun invoke(host: String, port: Int, username: String, key: String) {
        repository.connectViaKey(host, port, username, key)
    }
}