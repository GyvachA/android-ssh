package com.gyvacha.androidssh.ui.screens.form

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.gyvacha.androidssh.R
import com.gyvacha.androidssh.domain.model.ProxySpec
import com.gyvacha.androidssh.ui.components.TextFieldCharacterCount

@Composable
fun VlessConfigForm(
    config: ProxySpec.Vless,
    onUpdate: (ProxySpec.Vless) -> Unit,
    modifier: Modifier = Modifier,
    maxTextLength: Int = 60,
) {
    Column(
        modifier = modifier
    ) {
        TextFieldCharacterCount(
            value = config.uuid,
            onValueChange = { onUpdate(config.copy(uuid = it)) },
            label = { Text(stringResource(R.string.uuid)) },
            maxLength = maxTextLength,
            isError = config.uuid.isBlank()
        )

        TextFieldCharacterCount(
            value = config.flow.orEmpty(),
            onValueChange = { onUpdate(config.copy(flow = it.ifBlank { null })) },
            label = { Text(stringResource(R.string.flow)) },
            maxLength = maxTextLength,
            isError = false
        )

        Text("Reality")
        TextFieldCharacterCount(
            value = config.realityPublicKey.orEmpty(),
            onValueChange = { onUpdate(config.copy(realityPublicKey = it.ifBlank { null })) },
            label = { Text(stringResource(R.string.public_key)) },
            maxLength = maxTextLength,
            isError = false
        )
        TextFieldCharacterCount(
            value = config.realityShortId.orEmpty(),
            onValueChange = { onUpdate(config.copy(realityShortId = it.ifBlank { null })) },
            label = { Text(stringResource(R.string.short_id)) },
            maxLength = maxTextLength,
            isError = false
        )
        TextFieldCharacterCount(
            value = config.realityFingerprint.orEmpty(),
            onValueChange = { onUpdate(config.copy(realityFingerprint = it.ifBlank { null })) },
            label = { Text(stringResource(R.string.fingerprint)) },
            maxLength = maxTextLength,
            isError = false
        )
        TextFieldCharacterCount(
            value = config.realityServerName.orEmpty(),
            onValueChange = { onUpdate(config.copy(realityServerName = it.ifBlank { null })) },
            label = { Text(stringResource(R.string.server_name)) },
            maxLength = maxTextLength,
            isError = false
        )

        TransportForm(
            transport = config.transport,
            onUpdate = { onUpdate(config.copy(transport = it)) }
        )
    }
}
