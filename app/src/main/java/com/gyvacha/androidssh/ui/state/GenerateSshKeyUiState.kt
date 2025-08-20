package com.gyvacha.androidssh.ui.state

import com.gyvacha.androidssh.utils.SshKeyGenerator

data class GenerateSshKeyUiState(
    val sshKeyAlias: String = "Host Key",
    val sshKeyAlgorithm: String = SshKeyGenerator.Algorithm.ALGORITHM_ED25519.title,
    val sshKeyAlgorithmMenuExpanded: Boolean = false,
    val saveButtonEnabled: Boolean = true,
    val sshKeyPassphrase: String = "",
    val isPassphraseVisible: Boolean = false
)
