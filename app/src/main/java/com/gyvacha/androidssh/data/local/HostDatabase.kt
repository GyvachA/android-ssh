package com.gyvacha.androidssh.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.gyvacha.androidssh.data.local.dao.HostDao
import com.gyvacha.androidssh.data.local.dao.ProxyConfigDao
import com.gyvacha.androidssh.data.local.dao.SshKeyDao
import com.gyvacha.androidssh.data.local.entities.HostEntity
import com.gyvacha.androidssh.data.local.entities.ProxyConfigEntity
import com.gyvacha.androidssh.data.local.entities.SshKeyEntity

@Database(entities = [HostEntity::class, SshKeyEntity::class, ProxyConfigEntity::class], version = 1, exportSchema = true)
abstract class HostDatabase : RoomDatabase() {
    abstract fun hostDao(): HostDao
    abstract fun sshKeyDao(): SshKeyDao
    abstract fun proxyConfigDao(): ProxyConfigDao
}