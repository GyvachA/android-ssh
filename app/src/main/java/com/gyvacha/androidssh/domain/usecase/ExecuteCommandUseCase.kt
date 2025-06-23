package com.gyvacha.androidssh.domain.usecase

import com.gyvacha.androidssh.data.CommandExecutor
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ExecuteCommandUseCase @Inject constructor(
    private val executor: CommandExecutor
) {
    operator fun invoke(command: String): Flow<String> = executor.execute(command)
}