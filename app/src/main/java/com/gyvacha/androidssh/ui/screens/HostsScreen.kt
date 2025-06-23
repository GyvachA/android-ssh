package com.gyvacha.androidssh.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gyvacha.androidssh.R
import com.gyvacha.androidssh.domain.model.navigation.TopAppBarParams
import com.gyvacha.androidssh.ui.components.HostCard
import com.gyvacha.androidssh.ui.components.TopAppBarWithBackButton
import com.gyvacha.androidssh.ui.viewmodel.HostsViewModel

@Composable
fun HostsScreen(
    topAppBarParams: TopAppBarParams,
    viewModel: HostsViewModel = hiltViewModel()
) {
    val hosts by viewModel.hosts.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBarWithBackButton(
                topAppBarParams
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
//                .padding(dimensionResource(R.dimen.small_padding))
        ) {
            items(hosts) { host ->
                HostCard(host)
            }
        }
    }
}