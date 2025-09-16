package com.gyvacha.androidssh.ui.components

import android.graphics.Color
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.yandex.mobile.ads.nativeads.NativeAd
import com.yandex.mobile.ads.nativeads.template.NativeBannerView
import com.yandex.mobile.ads.nativeads.template.SizeConstraint
import com.yandex.mobile.ads.nativeads.template.appearance.BannerAppearance
import com.yandex.mobile.ads.nativeads.template.appearance.ButtonAppearance
import com.yandex.mobile.ads.nativeads.template.appearance.ImageAppearance
import com.yandex.mobile.ads.nativeads.template.appearance.NativeTemplateAppearance
import com.yandex.mobile.ads.nativeads.template.appearance.TextAppearance

@Suppress("MagicNumber")
@Composable
fun NativeBannerAd(
    nativeAd: NativeAd?,
    modifier: Modifier = Modifier
) {
    nativeAd?.let {
        AndroidView(
            modifier = modifier.fillMaxWidth(),
            factory = { context ->
                val adView = NativeBannerView(context)
                val appearance = NativeTemplateAppearance.Builder()
                    .withBannerAppearance(
                        BannerAppearance.Builder()
                            .setBorderColor(Color.YELLOW).build()
                    )
                    .withCallToActionAppearance(
                        ButtonAppearance.Builder()
                            .setTextAppearance(
                                TextAppearance.Builder()
                                    .setTextColor(Color.BLUE)
                                    .setTextSize(14f).build()
                            )
                            .setNormalColor(Color.TRANSPARENT)
                            .setPressedColor(Color.GRAY)
                            .setBorderColor(Color.BLUE)
                            .setBorderWidth(1f).build()
                    )
                    .withImageAppearance(
                        ImageAppearance.Builder()
                            .setWidthConstraint(
                                SizeConstraint(
                                    SizeConstraint.SizeConstraintType.FIXED,
                                    60f
                                )
                            ).build()
                    )
                    .withTitleAppearance(
                        TextAppearance.Builder()
                            .setTextColor(Color.BLACK)
                            .setTextSize(14f).build()
                    )
                    .withBodyAppearance(
                        TextAppearance.Builder()
                            .setTextColor(Color.GRAY)
                            .setTextSize(12f).build()
                    )
                    .build()
                adView.applyAppearance(appearance)
                adView.setAd(it)
                adView
            }
        )
    }
}
