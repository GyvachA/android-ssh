package com.gyvacha.androidssh.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.yandex.mobile.ads.banner.BannerAdView

@Composable
fun StickyBanner(
    banner: BannerAdView?,
    modifier: Modifier = Modifier,
) {
    banner?.let { adView ->
        AndroidView(
            modifier = modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface),
            factory = { adView }
        )
    }
}
