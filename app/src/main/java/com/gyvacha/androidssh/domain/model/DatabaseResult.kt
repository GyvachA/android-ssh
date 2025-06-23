package com.gyvacha.androidssh.domain.model


enum class DatabaseError {
    VALUE_ALREADY_EXISTS,
    UNKNOWN
}

sealed interface DatabaseResult {

    data class Error(val err: DatabaseError) : DatabaseResult
    data object Success : DatabaseResult
}