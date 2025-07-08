package com.gyvacha.androidssh.data.repository

import com.gyvacha.androidssh.domain.repository.SshRepository
import com.gyvacha.androidssh.utils.SshShellSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import net.schmizz.sshj.SSHClient
import net.schmizz.sshj.transport.verification.PromiscuousVerifier
import net.schmizz.sshj.userauth.password.PasswordFinder
import net.schmizz.sshj.userauth.password.Resource
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
        passphrase: String?
    ): Flow<String>? {
        return withContext(Dispatchers.IO) {
            try {
                sshSession?.close()
                val sshClient = SSHClient().apply {
                    connectTimeout = 10000
                    timeout = 10000
                    addHostKeyVerifier(PromiscuousVerifier())
                    connect(host, port)
                    var passwordFinder: PasswordFinder? = null
                    if (passphrase != null) {
                        passwordFinder = object : PasswordFinder {
                            override fun reqPassword(resource: Resource<*>?): CharArray {
                                return passphrase.toCharArray()
                            }

                            override fun shouldRetry(resource: Resource<*>?): Boolean {
                                return false
                            }
                        }
                    }
                    val keyProvider = loadKeys(
                        privateKey,
                        publicKey,
                        passwordFinder
                    )
                    authPublickey(username, keyProvider)
                }
                sshSession = SshShellSession(sshClient)
                sshSession?.welcomeFlow
            } catch (e: Exception) {
                throw e
            }

        }
    }

    override suspend fun connectViaPwd(
        host: String,
        port: Int,
        username: String,
        password: String
    ): Flow<String>? {
        return withContext(Dispatchers.IO) {
            sshSession?.close()
            val sshClient = SSHClient().apply {
                connectTimeout = 10000
                timeout = 10000
                addHostKeyVerifier(PromiscuousVerifier())
                connect(host, port)
                authPassword(username, password)
            }
            sshSession = SshShellSession(sshClient)
            sshSession?.welcomeFlow
        }
    }

    override suspend fun disconnect() {
        sshSession?.close()
        sshSession = null
    }

    override fun executeCommand(command: String): Flow<String> = flow {
        if (sshSession == null) throw IllegalStateException("Ssh session didn't initialized")
        emitAll(sshSession!!.executeCommand(command))
    }
}