package com.gyvacha.androidssh.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.gyvacha.androidssh.data.local.entities.SshKeyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SshKeyDao {
    @Query("SELECT ssh_key_id, alias, public_key FROM ssh_keys")
    fun getSshKeys(): Flow<List<SshKeyEntity>>

    @Insert
    suspend fun insertSshKey(sshKey: SshKeyEntity)

    @Delete
    suspend fun deleteSshKey(sshKey: SshKeyEntity)
}