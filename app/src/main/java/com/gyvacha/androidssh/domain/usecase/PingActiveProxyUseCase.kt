package com.gyvacha.androidssh.domain.usecase

import com.gyvacha.androidssh.domain.model.PingResult
import com.gyvacha.androidssh.domain.model.ProxyConfig
import com.gyvacha.androidssh.domain.repository.ProxyConfigRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.net.Socket
import javax.inject.Inject

class PingActiveProxyUseCase @Inject constructor(
    private val repository: ProxyConfigRepository
) {

    suspend operator fun invoke(timeoutMs: Int = 2000): PingResult {
        val activeConfig: ProxyConfig = repository.getActiveConfig() ?: return PingResult.Failure("No active proxy")

        val server = activeConfig.config.server
        val port = activeConfig.config.port

        return withContext(Dispatchers.IO) {
            try {
                val start = System.currentTimeMillis()
                Socket().use { socket ->
                    socket.connect(InetSocketAddress(server, port), timeoutMs)
                }
                val end = System.currentTimeMillis()
                PingResult.Success((end - start).toInt())
            } catch (e: Exception) {
                PingResult.Failure(e.message ?: "Unknown error")
            }
        }
    }
}
