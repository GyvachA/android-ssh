package com.gyvacha.androidssh.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.gyvacha.androidssh.domain.model.ProxySpec
import com.gyvacha.androidssh.domain.model.ProxyType
import com.gyvacha.androidssh.data.converter.ProxySpecConverter

@Entity(tableName = "proxy_configs")
@TypeConverters(ProxySpecConverter::class)
data class ProxyConfigEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    @ColumnInfo(name = "alias")
    val alias: String,
    @ColumnInfo(name = "type")
    val type: ProxyType,
    @ColumnInfo(name = "config")
    val config: ProxySpec,
    @ColumnInfo(name = "is_active")
    val isActive: Boolean = false
)
