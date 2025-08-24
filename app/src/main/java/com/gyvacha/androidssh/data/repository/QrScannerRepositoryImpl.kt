package com.gyvacha.androidssh.data.repository

import android.content.Context
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.gyvacha.androidssh.domain.model.QrScanResult
import com.gyvacha.androidssh.domain.repository.QrScannerRepository
import com.gyvacha.androidssh.utils.throttleFirst
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject

class QrScannerRepositoryImpl @Inject constructor(
    private val context: Context
) : QrScannerRepository {

    private val scanner = BarcodeScanning.getClient(
        BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()
    )

    private val _analysis by lazy {
        ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
    }

    @OptIn(ExperimentalGetImage::class)
    override fun scanImage(): Flow<QrScanResult> = callbackFlow {
        val analyzer = ImageAnalysis.Analyzer { image ->
            val mediaImage = image.image ?: run {
                image.close()
                return@Analyzer
            }

            val inputImage = InputImage.fromMediaImage(
                mediaImage,
                image.imageInfo.rotationDegrees
            )

            scanner.process(inputImage)
                .addOnSuccessListener { barcodes ->
                    val text = barcodes.firstOrNull()?.rawValue
                    if (!text.isNullOrBlank()) {
                        trySend(QrScanResult.Success(text))
                    }
                }
                .addOnFailureListener { e ->
                    trySend(QrScanResult.Error(e))
                }
                .addOnCompleteListener {
                    image.close()
                }
        }

        _analysis.setAnalyzer(ContextCompat.getMainExecutor(context), analyzer)

        awaitClose {
            _analysis.clearAnalyzer()
        }
    }
        .throttleFirst(SCAN_DEBOUNCE)
        .distinctUntilChanged()

    override fun getAnalysis(): ImageAnalysis = _analysis

    companion object {
        private const val SCAN_DEBOUNCE = 200L
    }
}
