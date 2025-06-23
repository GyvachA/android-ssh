package com.gyvacha.androidssh.domain.usecase

import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import com.gyvacha.androidssh.domain.model.DatabaseError
import com.gyvacha.androidssh.domain.model.DatabaseResult
import com.gyvacha.androidssh.domain.model.Host
import com.gyvacha.androidssh.domain.repository.HostRepository
import javax.inject.Inject

class InsertHostUseCase @Inject constructor(
    private val repository: HostRepository
) {
    suspend operator fun invoke(host: Host): DatabaseResult {
        try {
            repository.insertHost(host)
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