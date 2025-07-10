package com.gyvacha.androidssh.data.converter

import androidx.room.TypeConverter
import com.gyvacha.androidssh.domain.model.ProxySpec
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object ProxySpecConverter {
    @TypeConverter
    fun toJson(spec: ProxySpec): String = Json.encodeToString(spec)

    @TypeConverter
    fun fromJson(json: String): ProxySpec = Json.decodeFromString(json)
}