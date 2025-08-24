package com.gyvacha.androidssh.domain.model

sealed class QrScanResult {
    data class Success(val text: String) : QrScanResult()
    data class Error(val throwable: Throwable) : QrScanResult()
}
