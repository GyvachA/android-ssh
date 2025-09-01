package com.gyvacha.androidssh.data.repository

import com.gyvacha.androidssh.domain.repository.SshRepository
import com.gyvacha.androidssh.utils.SshShellSession
import com.jcraft.jsch.JSch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Singleton

@Singleton
class SshRepositoryImpl : SshRepository {

    private var sshSession: SshShellSession? = null

    override suspend fun connectViaKey(
        host: String,
        port: Int,
        username: String,
        privateKey: String,
        publicKey: String,
        passphrase: String?,
    ): Flow<String>? = withContext(Dispatchers.IO) {
        sshSession?.close()
        val jsch = JSch()
        jsch.addIdentity(
            "android-identity",
            privateKey.toByteArray(Charsets.UTF_8),
            publicKey.toByteArray(Charsets.UTF_8),
            passphrase?.toByteArray(Charsets.UTF_8)
        )

        val session = jsch.getSession(username, host, port)
        session.setConfig("StrictHostKeyChecking", "no")
        session.connect(CONNECTION_TIMEOUT)

        sshSession = SshShellSession(session)
        sshSession?.welcomeFlow
    }

    override suspend fun connectViaPwd(
        host: String,
        port: Int,
        username: String,
        password: String,
    ): Flow<String>? = withContext(Dispatchers.IO) {
        sshSession?.close()

        val jsch = JSch()
        val session = jsch.getSession(username, host, port)
        session.setPassword(password)
        session.setConfig("StrictHostKeyChecking", "no")
        session.connect(CONNECTION_TIMEOUT)

        sshSession = SshShellSession(session)
        sshSession?.welcomeFlow
    }

    override suspend fun disconnect() {
        sshSession?.close()
        sshSession = null
    }

    override fun executeCommand(command: String): Flow<String> = flow {
        val session = sshSession ?: error("SSH session not initialized")
        emitAll(session.executeCommand(command))
    }

    companion object {
        private const val CONNECTION_TIMEOUT = 5000
    }
}
