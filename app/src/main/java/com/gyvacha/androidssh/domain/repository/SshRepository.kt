package com.gyvacha.androidssh.domain.repository

import kotlinx.coroutines.flow.SharedFlow

interface SshRepository {
    suspend fun disconnect()
    suspend fun executeCommand(command: String)
    suspend fun connectViaKey(
        host: String,
        port: Int,
        username: String,
        privateKey: String,
        publicKey: String,
        passphrase: String?,
        onHostKeyReceived: suspend (String) -> Boolean
    ): SharedFlow<String>

    suspend fun connectViaPwd(
        host: String,
        port: Int,
        username: String,
        password: String,
        onHostKeyReceived: suspend (String) -> Boolean
    ): SharedFlow<String>
}
