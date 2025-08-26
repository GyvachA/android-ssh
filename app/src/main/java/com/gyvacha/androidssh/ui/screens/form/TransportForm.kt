package com.gyvacha.androidssh.ui.screens.form

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.gyvacha.androidssh.R
import com.gyvacha.androidssh.domain.model.Transport
import com.gyvacha.androidssh.ui.components.TextFieldCharacterCount

@Composable
fun TransportForm(
    transport: Transport,
    onUpdate: (Transport) -> Unit,
    modifier: Modifier = Modifier,
    maxTextLength: Int = 60,
) {
    Column(
        modifier = modifier
    ) {
        Text(stringResource(R.string.transport))
        Row {
            FilterChip(
                selected = transport is Transport.TCP,
                onClick = { onUpdate(Transport.TCP) },
                label = { Text(stringResource(R.string.tcp)) },
                modifier = Modifier.padding(end = dimensionResource(R.dimen.small_padding)),
                shape = RoundedCornerShape(dimensionResource(R.dimen.large_round_corner))
            )
            FilterChip(
                selected = transport is Transport.WS,
                onClick = { onUpdate(Transport.WS()) },
                label = { Text(stringResource(R.string.ws)) },
                modifier = Modifier.padding(end = dimensionResource(R.dimen.small_padding)),
                shape = RoundedCornerShape(dimensionResource(R.dimen.large_round_corner))
            )
            FilterChip(
                selected = transport is Transport.GRPC,
                onClick = { onUpdate(Transport.GRPC()) },
                label = { Text(stringResource(R.string.grpc)) },
                shape = RoundedCornerShape(dimensionResource(R.dimen.large_round_corner))
            )
        }

        when (transport) {
            is Transport.WS -> {
                TextFieldCharacterCount(
                    value = transport.path,
                    onValueChange = { onUpdate(transport.copy(path = it)) },
                    label = { Text(stringResource(R.string.ws_path)) },
                    maxLength = maxTextLength,
                    isError = transport.path.isBlank()
                )
                TextFieldCharacterCount(
                    value = transport.hostHeader,
                    onValueChange = { onUpdate(transport.copy(hostHeader = it)) },
                    label = { Text(stringResource(R.string.ws_host_header)) },
                    maxLength = maxTextLength,
                    isError = transport.hostHeader.isBlank()
                )
            }
            is Transport.GRPC -> {
                TextFieldCharacterCount(
                    value = transport.serviceName,
                    onValueChange = { onUpdate(transport.copy(serviceName = it)) },
                    label = { Text(stringResource(R.string.grpc_service_name)) },
                    maxLength = maxTextLength,
                    isError = transport.serviceName.isBlank()
                )
            }
            else -> {}
        }
    }
}
