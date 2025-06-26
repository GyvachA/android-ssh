package com.gyvacha.androidssh.domain.repository

import kotlinx.coroutines.flow.Flow

interface SshRepository {
    suspend fun connectViaKey(host: String, port:Int, username: String, key: String)
    suspend fun connectViaPwd(host: String, port: Int, username: String, password: String)
    suspend fun disconnect()
    fun executeCommand(command: String): Flow<String>
}