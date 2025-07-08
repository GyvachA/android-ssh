package com.gyvacha.androidssh.ui.utils

sealed class ViewEvent {
    data object NavigateUp : ViewEvent()
    data object HostInserted : ViewEvent()
    data object HostUpdated : ViewEvent()
    data object SshKeyCreated : ViewEvent()
    data object SshKeyCreateFailure : ViewEvent()
    data object DatabaseExceptionCaught : ViewEvent()
}