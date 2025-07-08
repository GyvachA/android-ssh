package com.gyvacha.androidssh.domain.repository

import kotlinx.coroutines.flow.Flow

interface SshRepository {
    suspend fun connectViaKey(host: String, port:Int, username: String, privateKey: String, publicKey: String, passphrase: String?): Flow<String>?
    suspend fun connectViaPwd(host: String, port: Int, username: String, password: String): Flow<String>?
    suspend fun disconnect()
    fun executeCommand(command: String): Flow<String>
}