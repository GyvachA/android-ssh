package com.gyvacha.androidssh.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gyvacha.androidssh.domain.usecase.GetSshKeysUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SshKeysViewModel @Inject constructor(
    getSshKeysUseCase: GetSshKeysUseCase,
) : ViewModel() {

    val sshKeys = getSshKeysUseCase()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
}
