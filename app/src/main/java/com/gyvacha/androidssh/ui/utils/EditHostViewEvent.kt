package com.gyvacha.androidssh.ui.utils

sealed class EditHostViewEvent {
    data object NavigateUp : EditHostViewEvent()
    data object HostInserted : EditHostViewEvent()
    data object HostUpdated : EditHostViewEvent()
    data object SshKeyCreated : EditHostViewEvent()
    data object SshKeyCreateFailure : EditHostViewEvent()
    data object DatabaseExceptionCaught : EditHostViewEvent()
}

sealed class SingboxViewEvent {
    data object RequestNotificationPermission : SingboxViewEvent()
    data object RequestVPNPermission : SingboxViewEvent()
}
