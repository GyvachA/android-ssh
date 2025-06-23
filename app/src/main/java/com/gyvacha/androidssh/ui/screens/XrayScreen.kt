package com.gyvacha.androidssh.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.gyvacha.androidssh.domain.model.navigation.TopAppBarParams
import com.gyvacha.androidssh.ui.components.TopAppBarWithBackButton

@Composable
fun XrayScreen(topAppBarParams: TopAppBarParams) {
    var changeText by rememberSaveable {
        mutableStateOf("Xray")
    }

    Scaffold(
        topBar = { TopAppBarWithBackButton(topAppBarParams) }
    ) { padding ->
        TextField(
            value = changeText,
            onValueChange = { changeText = it },
            modifier = Modifier.padding(padding)
        )
    }
}
