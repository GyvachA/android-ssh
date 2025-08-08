package com.gyvacha.androidssh.domain.repository

import com.gyvacha.androidssh.domain.model.Status
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface SingboxRepository {
    val logs: Flow<String>
    val serviceStatus: StateFlow<Status>
    suspend fun start(configPath: String)
    suspend fun stop()
}