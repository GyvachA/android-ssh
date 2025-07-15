package com.gyvacha.androidssh.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gyvacha.androidssh.domain.model.ProxyConfig
import com.gyvacha.androidssh.domain.usecase.DeleteConfigUseCase
import com.gyvacha.androidssh.domain.usecase.GetConfigsUseCase
import com.gyvacha.androidssh.domain.usecase.InsertConfigUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SingboxViewModel @Inject constructor(
    private val deleteConfigUseCase: DeleteConfigUseCase,
    private val insertConfigUseCase: InsertConfigUseCase,
    getConfigsUseCase: GetConfigsUseCase
) : ViewModel() {

    val configs = getConfigsUseCase()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun deleteConfig(config: ProxyConfig) {
        viewModelScope.launch {
            deleteConfigUseCase(config)
        }
    }

    fun insertConfig(config: ProxyConfig) {
        viewModelScope.launch {
            insertConfigUseCase(config)
        }
    }
}