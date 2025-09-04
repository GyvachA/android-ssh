package com.gyvacha.androidssh.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun TextFieldCharacterCount(
    value: String,
    onValueChange: (String) -> Unit,
    maxLength: Int,
    isError: Boolean,
    modifier: Modifier = Modifier,
    errorMessage: String? = null,
    enabled: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
    label:
    @Composable()
    (() -> Unit)? = null,
) {
    TextFieldBase(
        value = value,
        label = label,
        onValueChange = {
            if (it.length <= maxLength) onValueChange(it)
        },
        modifier = modifier.fillMaxWidth(),
        keyboardOptions = keyboardOptions,
        isError = isError,
        enabled = enabled,
        supportingText = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (errorMessage != null && isError) Text(text = errorMessage) else Spacer(modifier = Modifier)
                Text(text = "${value.length} / $maxLength")
            }
        }
    )
}
