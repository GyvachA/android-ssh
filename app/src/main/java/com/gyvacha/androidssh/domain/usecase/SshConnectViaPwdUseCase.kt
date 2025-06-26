package com.gyvacha.androidssh.domain.usecase

import com.gyvacha.androidssh.domain.repository.SshRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SshConnectViaPwdUseCase @Inject constructor(
    private val repository: SshRepository
) {
    suspend operator fun invoke(host: String, port: Int, username: String, password: String) {
        withContext(Dispatchers.IO) {
            repository.connectViaPwd(host, port, username, password)
        }
    }
}