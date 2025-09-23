package com.gyvacha.androidssh.data.repository

import android.util.Log
import com.gyvacha.androidssh.domain.repository.SshRepository
import com.gyvacha.androidssh.utils.FingerprintManager
import com.gyvacha.androidssh.utils.SshShellSession
import com.jcraft.jsch.JSch
import com.jcraft.jsch.UserInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Singleton

@Singleton
class SshRepositoryImpl(
    private val fingerprintManager: FingerprintManager
) : SshRepository {

    private var sshSession: SshShellSession? = null

    override suspend fun connectViaKey(
        host: String,
        port: Int,
        username: String,
        privateKey: String,
        publicKey: String,
        passphrase: String?,
        onHostKeyReceived: suspend (fingerprint: String) -> Boolean
    ): SharedFlow<String> = withContext(Dispatchers.IO) {
        sshSession?.close()
        val jsch = JSch()
        jsch.addIdentity(
            "android-identity",
            privateKey.toByteArray(Charsets.UTF_8),
            publicKey.toByteArray(Charsets.UTF_8),
            passphrase?.toByteArray(Charsets.UTF_8)
        )

        val session = jsch.getSession(username, host, port)
        session.setConfig("StrictHostKeyChecking", "ask")
        val hostKeyId = "$host:$port"
        session.userInfo = object : UserInfo {
            override fun getPassword(): String? = null
            override fun promptYesNo(message: String): Boolean {
                val fingerprint = session.hostKey.getFingerPrint(jsch)
                return runBlocking {
                    if (fingerprintManager.isKnown(hostKeyId, fingerprint)) {
                        true
                    } else {
                        val approved = onHostKeyReceived(fingerprint)
                        if (approved) fingerprintManager.add(hostKeyId, fingerprint)
                        approved
                    }
                }
            }
            override fun showMessage(message: String) {
                Log.d("UserInfo", "showMessage called")
            }
            override fun promptPassphrase(message: String?): Boolean = true
            override fun getPassphrase(): String? = null
            override fun promptPassword(message: String?): Boolean = false
        }
        session.connect(CONNECTION_TIMEOUT)

        sshSession = SshShellSession(session)
        sshSession?.outputFlow ?: error("Ssh session didn't initialized")
    }

    override suspend fun connectViaPwd(
        host: String,
        port: Int,
        username: String,
        password: String,
        onHostKeyReceived: suspend (fingerprint: String) -> Boolean
    ): SharedFlow<String> = withContext(Dispatchers.IO) {
        sshSession?.close()

        val jsch = JSch()
        val session = jsch.getSession(username, host, port)
        session.setPassword(password)
        session.setConfig("StrictHostKeyChecking", "ask")
        val hostKeyId = "$host:$port"
        session.userInfo = object : UserInfo {
            override fun getPassword(): String? = null
            override fun promptYesNo(message: String): Boolean {
                val fingerprint = session.hostKey.getFingerPrint(jsch)
                return runBlocking {
                    if (fingerprintManager.isKnown(hostKeyId, fingerprint)) {
                        true
                    } else {
                        val approved = onHostKeyReceived(fingerprint)
                        if (approved) fingerprintManager.add(hostKeyId, fingerprint)
                        approved
                    }
                }
            }
            override fun showMessage(message: String) {
                Log.d("UserInfo", "showMessage called")
            }
            override fun promptPassphrase(message: String?): Boolean = true
            override fun getPassphrase(): String? = null
            override fun promptPassword(message: String?): Boolean = false
        }
        session.connect(CONNECTION_TIMEOUT)

        sshSession = SshShellSession(session)
        sshSession?.outputFlow ?: error("Ssh session didn't initialized")
    }

    override suspend fun disconnect() {
        sshSession?.close()
        sshSession = null
    }

    override suspend fun executeCommand(command: String) {
        val session = sshSession ?: error("SSH session not initialized")
        session.executeCommand(command)
    }

    companion object {
        private const val CONNECTION_TIMEOUT = 5000
    }
}
