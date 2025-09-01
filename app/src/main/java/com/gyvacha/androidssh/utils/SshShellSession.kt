package com.gyvacha.androidssh.utils

import com.jcraft.jsch.ChannelShell
import com.jcraft.jsch.Session
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
import kotlin.coroutines.coroutineContext

class SshShellSession(
    private val session: Session,
    private val prompt: String = "__CMD_DONE__${System.currentTimeMillis()}"
) : AutoCloseable {

    private val channel: ChannelShell
    private val writer: BufferedWriter
    private val reader: BufferedReader

    val welcomeFlow: Flow<String>

    init {
        channel = session.openChannel("shell") as ChannelShell
        val inputStream = channel.inputStream
        val outputStream = channel.outputStream

        reader = BufferedReader(InputStreamReader(inputStream, StandardCharsets.UTF_8))
        writer = BufferedWriter(OutputStreamWriter(outputStream, StandardCharsets.UTF_8))

        channel.connect(CONNECTION_TIMEOUT)

        welcomeFlow = flow {
            val startTime = System.currentTimeMillis()
            while (System.currentTimeMillis() - startTime < WELCOME_TIMEOUT) {
                coroutineContext.ensureActive()
                if (reader.ready()) {
                    val line = reader.readLine() ?: break
                    if (line.isNotBlank()) {
                        val output = stripAnsi(line).replace(prompt, "")
                        emit(output)
                    }
                } else {
                    delay(OUTPUT_DELAY)
                }
            }
        }.flowOn(Dispatchers.IO)
    }

    fun executeCommand(command: String): Flow<String> = flow {
        sendRaw("$command; echo $prompt")
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            coroutineContext.ensureActive()
            val cleaned = stripAnsi(line ?: "")
            if (cleaned.contains(prompt)) break
            emit(cleaned)
        }
    }.flowOn(Dispatchers.IO)

    private fun sendRaw(command: String) {
        writer.write(command)
        writer.write("\n")
        writer.flush()
    }

    private fun stripAnsi(input: String): String =
        input.replace(Regex("\u001B\\[[;?\\d]*[a-zA-Z]"), "")

    override fun close() {
        channel.disconnect()
        session.disconnect()
    }

    companion object {
        private const val CONNECTION_TIMEOUT = 5000
        private const val WELCOME_TIMEOUT = 1000L
        private const val OUTPUT_DELAY = 50L
    }
}
