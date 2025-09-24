package com.gyvacha.androidssh.ui.screens

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import com.gyvacha.androidssh.R
import com.gyvacha.androidssh.domain.model.navigation.AppNavigation
import com.gyvacha.androidssh.domain.model.navigation.TopAppBarParams
import com.gyvacha.androidssh.ui.components.KnownHostCard
import com.gyvacha.androidssh.ui.components.SettingsCard
import com.gyvacha.androidssh.ui.components.SshKeyCard
import com.gyvacha.androidssh.ui.components.SshKeysBottomSheet
import com.gyvacha.androidssh.ui.components.TopAppBarWithBackButton
import com.gyvacha.androidssh.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    topAppBarParams: TopAppBarParams,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val isDialogOpen =
        (
            navBackStackEntry?.destination?.hasRoute<AppNavigation.EditSshKey>() == true ||
                navBackStackEntry?.destination?.hasRoute<AppNavigation.GenerateSshKey>() == true
            )

    Scaffold(
        topBar = { TopAppBarWithBackButton(topAppBarParams) },
        modifier = modifier
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(dimensionResource(R.dimen.medium_padding))
                .verticalScroll(scrollState)
        ) {
            SettingsCard(
                title = stringResource(R.string.ssh_keys),
                onCardClick = {
                    viewModel.updateSshKeyExtended(true)
                }
            )
            SettingsCard(
                title = stringResource(R.string.known_hosts),
                onCardClick = {
                    viewModel.updateKnownHostExtended(true)
                }
            )
            val context = LocalContext.current
            SettingsCard(
                title = stringResource(R.string.privacy_policy),
                onCardClick = {
                    val intent = Intent(Intent.ACTION_VIEW, "https://gyvacha.github.io/android-ssh/".toUri())
                    context.startActivity(intent)
                },
                cardImage = Icons.Filled.Info
            )
        }

        if (uiState.extendedSshKeys && !isDialogOpen) {
            SshKeysBottomSheet(
                onDismissRequest = { viewModel.updateSshKeyExtended(false) },
                generateSshKeyClick = {
                    navController.navigate(AppNavigation.GenerateSshKey)
                },
                addSshKeyClick = {
                    navController.navigate(AppNavigation.EditSshKey())
                },
                modifier = Modifier.padding(
                    top = padding.calculateTopPadding()
                )
            ) { sshKey ->
                SshKeyCard(
                    sshKey = sshKey,
                    onClick = {
                        navController.navigate(AppNavigation.EditSshKey(sshKey.sshKeyId))
                    },
                    isShowMenu = true,
                    onDeleteSshKey = { viewModel.deleteSshKey(sshKey) }
                )
            }
        }

        if (uiState.extendedKnownHosts) {
            val sheetState = rememberModalBottomSheetState()

            ModalBottomSheet(
                sheetState = sheetState,
                onDismissRequest = { viewModel.updateKnownHostExtended(false) },
                modifier = Modifier.padding(
                    top = padding.calculateTopPadding()
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(dimensionResource(R.dimen.medium_padding))
                ) {
                    LazyColumn {
                        if (uiState.knownHosts.isEmpty()) {
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
                        items(uiState.knownHosts.toList()) { (host, fingerprint) ->
                            KnownHostCard(
                                host = host,
                                fingerprint = fingerprint,
                                onDeleteKnownHost = { viewModel.deleteKnownHost(host) }
                            )
                        }
                    }
                }
            }
        }
    }
}
