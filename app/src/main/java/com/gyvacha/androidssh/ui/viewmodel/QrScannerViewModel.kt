package com.gyvacha.androidssh.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gyvacha.androidssh.domain.model.ProxyConfig
import com.gyvacha.androidssh.domain.model.QrScanResult
import com.gyvacha.androidssh.domain.usecase.InsertConfigUseCase
import com.gyvacha.androidssh.domain.usecase.ScanImageUseCase
import com.gyvacha.androidssh.ui.state.QrScannerUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QrScannerViewModel @Inject constructor(
    scanImageUseCase: ScanImageUseCase,
    private val insertConfigUseCase: InsertConfigUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(QrScannerUiState())
    val uiState = _uiState.asStateFlow()

    val analysis = scanImageUseCase.getAnalysis()

    init {
        scanImageUseCase()
            .onEach { result ->
                when (result) {
                    is QrScanResult.Error -> {}
                    is QrScanResult.Success -> {
                        _uiState.update {
                            it.copy(
                                qrText = result.text
                            )
                        }
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    fun insertConfig(config: ProxyConfig) {
        viewModelScope.launch {
            insertConfigUseCase(config)
        }
    }
}
