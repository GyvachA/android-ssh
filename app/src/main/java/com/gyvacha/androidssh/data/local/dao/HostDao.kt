package com.gyvacha.androidssh.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.gyvacha.androidssh.data.local.entities.HostEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HostDao {
    @Query("SELECT * FROM hosts")
    fun getHosts(): Flow<List<HostEntity>>

    @Insert
    suspend fun insertHost(host: HostEntity): Long

    @Delete
    suspend fun deleteHost(host: HostEntity): Int
}