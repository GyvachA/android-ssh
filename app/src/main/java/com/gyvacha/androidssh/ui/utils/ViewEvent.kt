package com.gyvacha.androidssh.ui.utils

import com.gyvacha.androidssh.domain.model.DatabaseError

sealed class ViewEvent {
    data object NavigateUp : ViewEvent()
    data object HostInserted : ViewEvent()
    data object SshKeyCreated : ViewEvent()
    data object SshKeyCreateFailure : ViewEvent()
    class DatabaseExceptionCaught(val databaseError: DatabaseError) : ViewEvent()
}