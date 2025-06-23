package com.gyvacha.androidssh.domain.usecase

import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import com.gyvacha.androidssh.domain.model.DatabaseError
import com.gyvacha.androidssh.domain.model.DatabaseResult
import com.gyvacha.androidssh.domain.model.SshKey
import com.gyvacha.androidssh.domain.repository.SshKeyRepository
import javax.inject.Inject

class InsertSshKeyUseCase @Inject constructor(
    private val repository: SshKeyRepository
) {
    suspend operator fun invoke(sshKey: SshKey): DatabaseResult {
        try {
            repository.insertSshKey(sshKey)
            return DatabaseResult.Success
        } catch (e: SQLiteConstraintException) {
            Log.e(InsertHostUseCase::class.java.simpleName, e.message, e)
            return DatabaseResult.Error(DatabaseError.VALUE_ALREADY_EXISTS)
        } catch (e: Exception) {
            Log.e(InsertHostUseCase::class.java.simpleName, e.message, e)
            return DatabaseResult.Error(DatabaseError.UNKNOWN)
        }
    }
}