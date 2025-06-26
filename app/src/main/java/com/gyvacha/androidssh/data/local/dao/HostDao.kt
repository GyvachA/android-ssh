package com.gyvacha.androidssh.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.gyvacha.androidssh.data.local.entities.HostEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HostDao {
    @Query("SELECT host_id, alias, host_name_or_ip, port, user_name FROM hosts")
    fun getHosts(): Flow<List<HostEntity>>

    @Query("SELECT * FROM hosts WHERE host_id = :hostId")
    suspend fun getHost(hostId: Int): HostEntity

    @Insert
    suspend fun insertHost(host: HostEntity)

    @Delete
    suspend fun deleteHost(host: HostEntity)
}