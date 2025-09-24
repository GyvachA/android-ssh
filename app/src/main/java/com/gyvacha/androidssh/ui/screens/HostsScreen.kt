package com.gyvacha.androidssh.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.gyvacha.androidssh.BuildConfig
import com.gyvacha.androidssh.R
import com.gyvacha.androidssh.domain.model.navigation.AppNavigation
import com.gyvacha.androidssh.domain.model.navigation.TopAppBarParams
import com.gyvacha.androidssh.ui.components.HostCard
import com.gyvacha.androidssh.ui.components.StickyBanner
import com.gyvacha.androidssh.ui.components.TopAppBarWithBackButton
import com.gyvacha.androidssh.ui.viewmodel.HostsViewModel
import com.gyvacha.androidssh.ui.viewmodel.StickyBannerViewModel

@Composable
fun HostsScreen(
    navController: NavController,
    topAppBarParams: TopAppBarParams,
    modifier: Modifier = Modifier,
    viewModel: HostsViewModel = hiltViewModel(),
    stickyBannerViewModel: StickyBannerViewModel = hiltViewModel()
) {
    val hosts by viewModel.hosts.collectAsStateWithLifecycle()
    val banner by stickyBannerViewModel.bannerAd.collectAsState()
    LaunchedEffect(Unit) {
        stickyBannerViewModel.loadBanner(BuildConfig.YANDEX_AD_BANNER_ID_SECOND)
    }
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBarWithBackButton(
                topAppBarParams
            )
        },
        bottomBar = {
            StickyBanner(
                banner = banner
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
        ) {
            LazyColumn {
                if (hosts.isEmpty()) {
                    item {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = stringResource(R.string.empty_yet),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
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
