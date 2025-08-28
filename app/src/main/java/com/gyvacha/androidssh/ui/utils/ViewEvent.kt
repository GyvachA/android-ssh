package com.gyvacha.androidssh.ui.utils

sealed class EditHostViewEvent {
    data object NavigateUp : EditHostViewEvent()
    data object HostInserted : EditHostViewEvent()
    data object HostUpdated : EditHostViewEvent()
    data object SshKeyCreated : EditHostViewEvent()
    data object SshKeyCreateFailure : EditHostViewEvent()
    data object DatabaseExceptionCaught : EditHostViewEvent()
}

sealed class GenerateSshKeyViewEvent {
    data object SshKeyCreated : GenerateSshKeyViewEvent()
    data object SshKeyCreateFailure : GenerateSshKeyViewEvent()
}

sealed class SingboxViewEvent {
    data object ServiceStared : SingboxViewEvent()
    data object ServiceStartError : SingboxViewEvent()
    data object ServiceErrorNoActiveConfig : SingboxViewEvent()
}
