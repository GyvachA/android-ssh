package com.gyvacha.androidssh.data.local.entities

import androidx.room.Embedded
import androidx.room.Relation

data class HostWithSshKeyEntity (
    @Embedded val host: HostEntity,
    @Relation(
        parentColumn = "ssh_key_id",
        entityColumn  = "ssh_key_id"
    )
    val sshKey: SshKeyEntity?
)