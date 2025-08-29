package com.gyvacha.androidssh.ui.screens.form

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.gyvacha.androidssh.R
import com.gyvacha.androidssh.domain.model.ProxySpec
import com.gyvacha.androidssh.ui.components.TextFieldCharacterCount
import com.gyvacha.androidssh.ui.components.TextFieldErrors
import com.gyvacha.androidssh.ui.components.getTextFieldErrorMessage

@Composable
fun VmessConfigForm(
    config: ProxySpec.Vmess,
    onUpdate: (ProxySpec.Vmess) -> Unit,
    modifier: Modifier = Modifier,
    maxTextLength: Int = 60,
) {
    Column(
        modifier = modifier
    ) {
        TextFieldCharacterCount(
            value = config.uuid,
            onValueChange = { onUpdate(config.copy(uuid = it)) },
            label = { Text(stringResource(R.string.uuid) + "*") },
            maxLength = maxTextLength,
            isError = config.uuid.isBlank(),
            errorMessage = getTextFieldErrorMessage(TextFieldErrors.STRING_BLANK_ERROR)
        )

        TextFieldCharacterCount(
            value = config.alterId.toString(),
            onValueChange = { new ->
                val safe = new.filter { it.isDigit() }.toIntOrNull() ?: 0
                onUpdate(config.copy(alterId = safe))
            },
            label = { Text(stringResource(R.string.alter_id)) },
            maxLength = 3,
            isError = false
        )

        TextFieldCharacterCount(
            value = config.security,
            onValueChange = { onUpdate(config.copy(security = it)) },
            label = { Text(stringResource(R.string.security) + "*") },
            maxLength = maxTextLength,
            isError = config.security.isBlank(),
            errorMessage = getTextFieldErrorMessage(TextFieldErrors.STRING_BLANK_ERROR)
        )

        TransportForm(
            transport = config.transport,
            onUpdate = { onUpdate(config.copy(transport = it)) }
        )
    }
}
