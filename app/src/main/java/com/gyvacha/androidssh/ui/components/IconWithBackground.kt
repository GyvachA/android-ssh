package com.gyvacha.androidssh.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import com.gyvacha.androidssh.R

@Composable
fun IconWithBackground(
    imageVector: ImageVector,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(56.dp)
            .clip(RoundedCornerShape(dimensionResource(R.dimen.small_round_corner))),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = imageVector,
            tint = MaterialTheme.colorScheme.surfaceTint,
            modifier = Modifier.size(40.dp),
            contentDescription = null
        )
    }
}
