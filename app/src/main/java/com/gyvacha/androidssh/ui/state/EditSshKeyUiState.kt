package com.gyvacha.androidssh.ui.state

import com.gyvacha.androidssh.domain.model.SshKey

data class EditSshKeyUiState(
    val sshKey: SshKey = SshKey(
        alias = "",
        publicKey = "",
        privateKey = "",
    ),
    val isPassphraseVisible: Boolean = false,
    val isPrivateKeyVisible: Boolean = false,
    val isSaveButtonEnabled: Boolean = false,
) {
    fun validate(): EditSshKeyUiState {
        val aliasValid = sshKey.alias.isNotBlank()
        val privateKeyValid = sshKey.privateKey.isNotBlank()
        val publicKeyValid = sshKey.publicKey.isNotBlank()
        return copy(
            isSaveButtonEnabled = aliasValid && privateKeyValid && publicKeyValid
        )
    }
}
