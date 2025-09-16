package com.gyvacha.androidssh.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.feed.FeedAd
import com.yandex.mobile.ads.feed.FeedAdAppearance
import com.yandex.mobile.ads.feed.FeedAdLoadListener
import com.yandex.mobile.ads.feed.FeedAdRequestConfiguration
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import kotlin.math.roundToInt

@Suppress("MagicNumber")
@HiltViewModel
class FeedAdsViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context
) : ViewModel() {

    private val _feedAd = MutableStateFlow<FeedAd?>(null)
    val feedAd: StateFlow<FeedAd?> = _feedAd

    fun loadFeed(adUnitId: String) {
        val metrics = context.resources.displayMetrics
        val screenWidthDp = (metrics.widthPixels / metrics.density).roundToInt()

        val margin = 8
        val cardWidthDp = screenWidthDp - margin * 2

        val appearance = FeedAdAppearance.Builder(cardWidthDp)
            .setCardCornerRadius(16.0)
            .build()
        val config = FeedAdRequestConfiguration.Builder(adUnitId).build()

        val feedAd = FeedAd.Builder(context, config, appearance).build()
        feedAd.loadListener = object : FeedAdLoadListener {
            override fun onAdLoaded() {
                _feedAd.value = feedAd
                Log.i("YandexAds", "FeedAd loaded successfully")
            }

            override fun onAdFailedToLoad(error: AdRequestError) {
                Log.e("YandexAds", "FeedAd load failed: $error")
            }
        }

        feedAd.preloadAd()
    }
}
