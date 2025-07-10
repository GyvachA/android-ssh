package com.gyvacha.androidssh.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.gyvacha.androidssh.data.local.entities.ProxyConfigEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProxyConfigDao {
    @Transaction
    suspend fun setActiveConfig(config: ProxyConfigEntity) {
        clearActive()
        activateById(config.id)
    }

    @Query("UPDATE proxy_configs SET is_active = 0 WHERE is_active = 1")
    suspend fun clearActive()

    @Query("UPDATE proxy_configs SET is_active = 1 WHERE id = :id")
    suspend fun activateById(id: Long)

    @Query("SELECT * FROM proxy_configs WHERE is_active = 1")
    suspend fun getActive(): ProxyConfigEntity?

    @Query("SELECT * FROM proxy_configs")
    fun getConfigs(): Flow<List<ProxyConfigEntity>>

    @Insert
    suspend fun insert(config: ProxyConfigEntity)

    @Update
    suspend fun update(config: ProxyConfigEntity)

    @Delete
    suspend fun delete(config: ProxyConfigEntity)
}
