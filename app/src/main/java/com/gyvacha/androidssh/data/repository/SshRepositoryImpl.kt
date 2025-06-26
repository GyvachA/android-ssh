package com.gyvacha.androidssh.data.repository

import com.gyvacha.androidssh.domain.repository.SshRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import net.schmizz.sshj.SSHClient
import net.schmizz.sshj.transport.verification.PromiscuousVerifier
import javax.inject.Singleton

@Singleton
class SshRepositoryImpl : SshRepository {
    private var sshClient: SSHClient? = null

    override suspend fun connectViaKey(host: String, port: Int, username: String, key: String) {
        withContext(Dispatchers.IO) {
            sshClient?.disconnect()
            sshClient = SSHClient().apply {
                addHostKeyVerifier(PromiscuousVerifier())
                connect(host, port)
                val keyProvider = loadKeys(key)
                authPublickey(username, keyProvider)
            }
        }
    }

    override suspend fun connectViaPwd(host: String, port: Int, username: String, password: String) {
        withContext(Dispatchers.IO) {
            sshClient?.disconnect()
            sshClient = SSHClient().apply {
                addHostKeyVerifier(PromiscuousVerifier())
                connect(host, port)
                authPassword(username, password)
            }
        }
    }

    override suspend fun disconnect() {
        sshClient?.disconnect()
        sshClient = null
    }

    override fun executeCommand(command: String): Flow<String> = flow {
        val session = sshClient?.startSession() ?: throw IllegalStateException("No SSH session established. Please check your credentials")
        val cmd = session.exec(command)
        val output = cmd.inputStream.bufferedReader().readText()
        emit(output)
    }.flowOn(Dispatchers.IO)
}