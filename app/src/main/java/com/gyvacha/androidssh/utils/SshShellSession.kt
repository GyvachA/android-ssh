package com.gyvacha.androidssh.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import net.schmizz.sshj.SSHClient
import net.schmizz.sshj.connection.channel.direct.Session
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.Closeable
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import kotlin.coroutines.coroutineContext

class SshShellSession(
    private val sshClient: SSHClient,
    private val prompt: String = "__CMD_DONE__${System.currentTimeMillis()}"
) : Closeable {
    private var session: Session
    private var shell: Session.Shell
    private var writer: BufferedWriter
    private var reader: BufferedReader
    var welcomeFlow: Flow<String>

    init {
        try {
            session = sshClient.startSession().apply { allocateDefaultPTY() }
            shell = session.startShell()
            writer = BufferedWriter(OutputStreamWriter(shell.outputStream))
            reader = BufferedReader(InputStreamReader(shell.inputStream))
            welcomeFlow = flow {
                val timeoutMs = 1000L
                val startTime = System.currentTimeMillis()

                while (System.currentTimeMillis() - startTime < timeoutMs) {
                    coroutineContext.ensureActive()
                    if (reader.ready()) {
                        val line = reader.readLine() ?: break
                        if (line.isNotBlank()) emit(line)
                    } else {
                        delay(50)
                    }
                }
            }.flowOn(Dispatchers.IO)
        } catch (e: Exception) {
            sshClient.disconnect()
            throw RuntimeException("Failed to initialize SSH shell session: ${e.message}", e)
        }
    }

    fun executeCommand(command: String): Flow<String> = flow {
        sendRaw(command)
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            coroutineContext.ensureActive()
            val currentLine = stripAnsi(line ?: "")
            if (currentLine.contains(prompt)) break
            emit(currentLine)
        }
    }.flowOn(Dispatchers.IO)

    private fun sendRaw(command: String) {
        writer.write(command)
        writer.write("\n")
        writer.flush()
    }

    override fun close() {
        shell.close()
        session.close()
        sshClient.disconnect()
    }

    private fun stripAnsi(input: String): String {
        return input.replace(Regex("\u001B\\[[;?\\d]*[a-zA-Z]"), "")
    }
}