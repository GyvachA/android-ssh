package com.gyvacha.androidssh.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommandExecutor @Inject constructor() {
    fun execute(command: String): Flow<String> {
        return flow {
            try {
                val process = ProcessBuilder(command.split(" "))
                    .redirectErrorStream(true)
                    .start()
                process.inputStream.bufferedReader().use { reader ->
                    var line: String
                    while (reader.readLine().also { if (it == null) line = "" else line = it } != null) {
                        emit(line)
                    }
                }
                process.waitFor()
            } catch (e: Exception) {
                emit("Ошибка: ${e.localizedMessage}")
            }
        }.flowOn(Dispatchers.IO)
    }
}