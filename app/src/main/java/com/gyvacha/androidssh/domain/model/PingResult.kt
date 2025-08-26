package com.gyvacha.androidssh.domain.model

sealed class PingResult {
    data class Success(val ms: Int) : PingResult()
    data class Failure(val error: String) : PingResult()
    object Idle : PingResult()
}
