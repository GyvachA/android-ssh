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
fun SocksConfigForm(
    config: ProxySpec.Socks,
    onUpdate: (ProxySpec.Socks) -> Unit,
    modifier: Modifier = Modifier,
    maxTextLength: Int = 60,
) {
    Column(
        modifier = modifier
    ) {
        TextFieldCharacterCount(
            value = config.username.orEmpty(),
            onValueChange = { onUpdate(config.copy(username = it.ifBlank { null })) },
            label = { Text(stringResource(R.string.user_name)) },
            maxLength = maxTextLength,
            isError = false
        )

        TextFieldCharacterCount(
            value = config.password.orEmpty(),
            onValueChange = { onUpdate(config.copy(password = it.ifBlank { null })) },
            label = { Text(stringResource(R.string.password)) },
            maxLength = maxTextLength,
            isError = false
        )
    }
}
