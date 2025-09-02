package com.gyvacha.androidssh.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import com.gyvacha.androidssh.R
import com.gyvacha.androidssh.domain.model.navigation.AppNavigation
import com.gyvacha.androidssh.domain.model.navigation.TopAppBarParams
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
    }
}
