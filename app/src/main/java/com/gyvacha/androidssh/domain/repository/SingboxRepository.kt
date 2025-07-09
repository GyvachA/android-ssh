package com.gyvacha.androidssh.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface SingboxRepository {
    val logs: Flow<String>
    val isRunning: StateFlow<Boolean>
    suspend fun start(configPath: String)
    suspend fun stop()
}