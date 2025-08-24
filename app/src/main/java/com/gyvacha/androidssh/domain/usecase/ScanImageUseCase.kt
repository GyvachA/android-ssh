package com.gyvacha.androidssh.domain.usecase

import androidx.camera.core.ImageAnalysis
import com.gyvacha.androidssh.domain.model.QrScanResult
import com.gyvacha.androidssh.domain.repository.QrScannerRepository
import kotlinx.coroutines.flow.Flow

class ScanImageUseCase(private val repository: QrScannerRepository) {
    operator fun invoke(): Flow<QrScanResult> {
        return repository.scanImage()
    }
    fun getAnalysis(): ImageAnalysis = repository.getAnalysis()
}
