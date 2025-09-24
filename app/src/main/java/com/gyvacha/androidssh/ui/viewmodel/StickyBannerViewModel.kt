package com.gyvacha.androidssh.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.yandex.mobile.ads.banner.BannerAdEventListener
import com.yandex.mobile.ads.banner.BannerAdSize
import com.yandex.mobile.ads.banner.BannerAdView
import com.yandex.mobile.ads.common.AdRequest
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class StickyBannerViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context
) : ViewModel() {

    private val _bannerAd = MutableStateFlow<BannerAdView?>(null)
    val bannerAd: StateFlow<BannerAdView?> = _bannerAd.asStateFlow()

    fun loadBanner(adUnitId: String) {
        val metrics = context.resources.displayMetrics
        val screenWidthPx = metrics.widthPixels
        val adWidth = (screenWidthPx / metrics.density).roundToInt()

        val adSize = BannerAdSize.stickySize(context, adWidth)

        val banner = BannerAdView(context).apply {
            setAdUnitId(adUnitId)
            setAdSize(adSize)
            setBannerAdEventListener(object : BannerAdEventListener {
                override fun onAdLoaded() {
                    _bannerAd.value = this@apply
                    Log.i("YandexAds", "Sticky banner loaded")
                }

                override fun onAdFailedToLoad(error: AdRequestError) {
                    Log.e("YandexAds", "Failed to load banner: $error")
                }

                override fun onAdClicked() {
                    loadAd(AdRequest.Builder().build())
                }
                override fun onLeftApplication() = Unit
                override fun onReturnedToApplication() = Unit
                override fun onImpression(impressionData: ImpressionData?) = Unit
            })
            loadAd(AdRequest.Builder().build())
        }

        _bannerAd.value = banner
    }

    override fun onCleared() {
        _bannerAd.value?.destroy()
        super.onCleared()
    }
}
