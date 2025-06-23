package com.gyvacha.androidssh.domain.model.navigation

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable

data class TopAppBarParams(
    val screenTitle: String,
    val canNavigateBack: Boolean,
    val navigateUp: () -> Unit,
    val actions: @Composable RowScope.() -> Unit = {}
) {
    companion object {
        val PREVIEW = TopAppBarParams(
            screenTitle = "Preview",
            canNavigateBack = true,
            navigateUp = {},
        )
    }
}