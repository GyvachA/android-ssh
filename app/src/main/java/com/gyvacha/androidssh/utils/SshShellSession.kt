package com.gyvacha.androidssh.utils

import com.jcraft.jsch.ChannelShell
import com.jcraft.jsch.Session
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets

class SshShellSession(
    private val session: Session
) : AutoCloseable {

    private val channel: ChannelShell = session.openChannel("shell") as ChannelShell
    private val writer: BufferedWriter
    private val reader: BufferedReader

    private val _outputFlow = MutableSharedFlow<String>(extraBufferCapacity = 1000)
    val outputFlow: SharedFlow<String> = _outputFlow.asSharedFlow()

    init {
        val inputStream = channel.inputStream
        val outputStream = channel.outputStream
        reader = BufferedReader(InputStreamReader(inputStream, StandardCharsets.UTF_8))
        writer = BufferedWriter(OutputStreamWriter(outputStream, StandardCharsets.UTF_8))
        channel.connect(CONNECTION_TIMEOUT)

        CoroutineScope(Dispatchers.IO).launch {
            val buffer = CharArray(BUFFER_SIZE)
            while (isActive && !channel.isClosed) {
                if (reader.ready()) {
                    val count = reader.read(buffer)
                    if (count == -1) break
                    val chunk = String(buffer, 0, count)
                    val cleaned = chunk
                    if (cleaned.isNotEmpty()) {
                        _outputFlow.emit(cleaned)
                    }
                } else {
                    delay(OUTPUT_DELAY)
                }
            }
        }
    }

    suspend fun executeCommand(command: String) = withContext(Dispatchers.IO) {
        writer.write(command)
        writer.write("\n")
        writer.flush()
    }

    override fun close() {
        channel.disconnect()
        session.disconnect()
    }

    companion object {
        private const val CONNECTION_TIMEOUT = 5000
        private const val OUTPUT_DELAY = 50L
        private const val BUFFER_SIZE = 8192
    }
}
