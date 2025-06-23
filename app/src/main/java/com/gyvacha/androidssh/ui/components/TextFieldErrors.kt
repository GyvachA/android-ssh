package com.gyvacha.androidssh.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.gyvacha.androidssh.R

enum class TextFieldErrors {
    STRING_LENGTH_ERROR,
    STRING_BLANK_ERROR
}

@Composable
fun getTextFieldErrorMessage(errorType: TextFieldErrors?) = when(errorType) {
    TextFieldErrors.STRING_LENGTH_ERROR -> stringResource(R.string.string_len_error)
    TextFieldErrors.STRING_BLANK_ERROR -> stringResource(R.string.string_blank_error)
    else -> null
}
