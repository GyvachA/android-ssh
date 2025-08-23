package com.gyvacha.androidssh.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.gyvacha.androidssh.data.local.entities.SshKeyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SshKeyDao {
    @Query("SELECT * FROM ssh_keys")
    fun getSshKeys(): Flow<List<SshKeyEntity>>

    @Query("SELECT * FROM ssh_keys WHERE ssh_key_id = :sshKeyId")
    suspend fun getSshKey(sshKeyId: Int): SshKeyEntity

    @Insert
    suspend fun insertSshKey(sshKey: SshKeyEntity): Long

    @Delete
    suspend fun deleteSshKey(sshKey: SshKeyEntity)

    @Update
    suspend fun updateSshKey(sshKey: SshKeyEntity)
}
