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
fun ShadowsocksConfigForm(
    config: ProxySpec.Shadowsocks,
    onUpdate: (ProxySpec.Shadowsocks) -> Unit,
    modifier: Modifier = Modifier,
    maxTextLength: Int = 60,
) {
    Column(
        modifier = modifier
    ) {
        TextFieldCharacterCount(
            value = config.method,
            onValueChange = { onUpdate(config.copy(method = it)) },
            label = { Text(stringResource(R.string.method_cipher) + "*") },
            maxLength = maxTextLength,
            isError = config.method.isBlank(),
            errorMessage = getTextFieldErrorMessage(TextFieldErrors.STRING_BLANK_ERROR)
        )

        TextFieldCharacterCount(
            value = config.password,
            onValueChange = { onUpdate(config.copy(password = it)) },
            label = { Text(stringResource(R.string.password) + "*") },
            maxLength = maxTextLength,
            isError = config.password.isBlank(),
            errorMessage = getTextFieldErrorMessage(TextFieldErrors.STRING_BLANK_ERROR)
        )
    }
}
