package com.gyvacha.androidssh.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.gyvacha.androidssh.data.local.entities.HostEntity
import com.gyvacha.androidssh.data.local.entities.HostWithSshKeyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HostDao {
    @Query("SELECT host_id, alias, host_name_or_ip, port, user_name, auth_type FROM hosts")
    fun getHosts(): Flow<List<HostEntity>>

    @Query("SELECT * FROM hosts WHERE host_id = :hostId")
    suspend fun getHost(hostId: Int): HostEntity

    @Transaction
    @Query("SELECT * FROM hosts WHERE host_id = :hostId")
    suspend fun getHostWithSshKey(hostId: Int): HostWithSshKeyEntity

    @Insert
    suspend fun insertHost(host: HostEntity)

    @Delete
    suspend fun deleteHost(host: HostEntity)
}