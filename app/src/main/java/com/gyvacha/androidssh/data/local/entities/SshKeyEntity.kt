package com.gyvacha.androidssh.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ssh_keys")
data class SshKeyEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ssh_key_id")
    val sshKeyId: Int = 0,
    @ColumnInfo(name = "alias")
    val alias: String,
    @ColumnInfo(name = "public_key")
    val publicKey: String,
    @ColumnInfo(name = "private_key")
    val privateKey: String?,
    @ColumnInfo(name = "passphrase")
    val passphrase: String?
)