package com.gyvacha.androidssh.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.gyvacha.androidssh.R
import com.gyvacha.androidssh.domain.model.navigation.TopAppBarParams

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarWithBackButton(topAppBarParams: TopAppBarParams, modifier: Modifier = Modifier) {
    TopAppBar(
        title = { Text(topAppBarParams.screenTitle) },
        modifier = modifier,
        navigationIcon = {
            if (topAppBarParams.canNavigateBack) {
                IconButton(onClick = topAppBarParams.navigateUp ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        },
        actions = topAppBarParams.actions
    )
}
