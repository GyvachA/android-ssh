package com.gyvacha.androidssh.ui.screens

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.gyvacha.androidssh.R
import com.gyvacha.androidssh.domain.model.ProxyConfig
import com.gyvacha.androidssh.domain.model.ProxySpec
import com.gyvacha.androidssh.domain.model.ProxyType
import com.gyvacha.androidssh.ui.components.PermissionDialog
import com.gyvacha.androidssh.ui.components.QrCameraView
import com.gyvacha.androidssh.ui.viewmodel.QrScannerViewModel
import com.gyvacha.androidssh.utils.LocalMessageNotifier
import com.gyvacha.androidssh.utils.ParseProxyConfig.parseProxyUri

@Composable
fun QrScannerScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: QrScannerViewModel = hiltViewModel()
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val snackbarManager = LocalMessageNotifier.current
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    val permLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            hasCameraPermission = true
        } else {
            val activity = context as Activity
            val canRequest = ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                Manifest.permission.CAMERA
            )
            if (!canRequest) {
                viewModel.updateShowPermissionRationale(true)
            } else {
                navController.navigateUp()
            }
        }
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    if (hasCameraPermission) {
        Scaffold(modifier = modifier) { padding ->
            QrCameraView(
                modifier = Modifier.padding(padding),
                lifecycleOwner = lifecycleOwner,
                analysis = viewModel.analysis
            )
        }

        LaunchedEffect(uiState.qrText) {
            if (uiState.qrText.isNotBlank()) {
                val proxy = parseProxyUri(uiState.qrText)
                if (proxy != null) {
                    val (alias, proxySpec) = proxy
                    viewModel.insertConfig(
                        ProxyConfig(
                            id = 0,
                            alias = alias,
                            type = when (proxySpec) {
                                is ProxySpec.Vless -> ProxyType.VLESS
                                is ProxySpec.Vmess -> ProxyType.VMESS
                                is ProxySpec.Trojan -> ProxyType.TROJAN
                                is ProxySpec.Shadowsocks -> ProxyType.SHADOWSOCKS
                                is ProxySpec.Socks -> ProxyType.SOCKS
                                is ProxySpec.Http -> ProxyType.HTTP
                            },
                            config = proxySpec,
                            isActive = false
                        )
                    )
                } else {
                    snackbarManager?.showSnackbar(
                        context.getString(R.string.error_get_config_from_copyboard)
                    )
                }
                navController.navigateUp()
            }
        }
    }
    if (uiState.showPermissionRationale) {
        PermissionDialog(
            explanationText = stringResource(R.string.camera_permission_explanation),
            onDismiss = {
                viewModel.updateShowPermissionRationale(false)
                navController.navigateUp()
            }
        )
    }
}
