package com.gyvacha.androidssh.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gyvacha.androidssh.domain.model.Host
import com.gyvacha.androidssh.domain.usecase.GetHostsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HostsViewModel @Inject constructor(
    private val getHostsUseCase: GetHostsUseCase
) : ViewModel() {

    val hosts: StateFlow<List<Host>> = getHostsUseCase()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
}