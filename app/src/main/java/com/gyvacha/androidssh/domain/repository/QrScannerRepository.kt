package com.gyvacha.androidssh.domain.repository

import androidx.camera.core.ImageAnalysis
import com.gyvacha.androidssh.domain.model.QrScanResult
import kotlinx.coroutines.flow.Flow

interface QrScannerRepository {
    fun scanImage(): Flow<QrScanResult>
    fun getAnalysis(): ImageAnalysis
}
