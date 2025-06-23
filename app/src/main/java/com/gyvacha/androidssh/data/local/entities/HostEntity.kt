package com.gyvacha.androidssh.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "hosts",
    foreignKeys = [
        ForeignKey(
            entity = SshKeyEntity::class,
            parentColumns = arrayOf("ssh_key_id"),
            childColumns = arrayOf("ssh_key_id"),
            onDelete = ForeignKey.SET_NULL,
            onUpdate = ForeignKey.CASCADE
        )],
    indices = [Index(value = ["ssh_key_id"])]
)
data class HostEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "host_id")
    val hostId: Int = 0,
    @ColumnInfo(name = "alias")
    val alias: String,
    @ColumnInfo(name = "host_name_or_ip")
    val hostNameOrIp: String,
    @ColumnInfo(name = "port")
    val port: Int,
    @ColumnInfo(name = "user_name")
    val userName: String,
    @ColumnInfo(name = "password")
    val password: String?,
    @ColumnInfo(name = "ssh_key_id")
    val sshKey: Int?
)