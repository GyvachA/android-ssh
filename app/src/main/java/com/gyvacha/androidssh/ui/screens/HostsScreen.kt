package com.gyvacha.androidssh.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.gyvacha.androidssh.domain.model.navigation.AppNavigation
import com.gyvacha.androidssh.domain.model.navigation.TopAppBarParams
import com.gyvacha.androidssh.ui.components.HostCard
import com.gyvacha.androidssh.ui.components.NativeBannerAd
import com.gyvacha.androidssh.ui.components.TopAppBarWithBackButton
import com.gyvacha.androidssh.ui.viewmodel.HostsViewModel

@Composable
fun HostsScreen(
    navController: NavController,
    topAppBarParams: TopAppBarParams,
    modifier: Modifier = Modifier,
    viewModel: HostsViewModel = hiltViewModel()
) {
    val hosts by viewModel.hosts.collectAsStateWithLifecycle()
    val nativeAd by viewModel.nativeAd.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewModel.loadNativeAd()
    }
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBarWithBackButton(
                topAppBarParams
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
        ) {
            NativeBannerAd(nativeAd)
            LazyColumn {
                itemsIndexed(hosts) { index, host ->
                    HostCard(
                        host = host,
                        onStartTerminal = {
                            navController.navigate(AppNavigation.HostsRoute.Terminal(host.hostId))
                        },
                        onCardClick = {
                            navController.navigate(AppNavigation.HostsRoute.Terminal(host.hostId))
                        },
                        onDeleteHost = {
                            viewModel.deleteHost(it)
                        },
                        onEditHost = {
                            navController.navigate(AppNavigation.HostsRoute.EditHost(host.hostId))
                        }
                    )
                }
            }
        }
    }
}
