package com.gyvacha.androidssh.ui.components

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gyvacha.androidssh.R
import com.yandex.mobile.ads.common.ImpressionData
import com.yandex.mobile.ads.feed.FeedAd
import com.yandex.mobile.ads.feed.FeedAdAdapter
import com.yandex.mobile.ads.feed.FeedAdEventListener

@Composable
fun FeedAdList(
    feedAd: FeedAd?,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    feedAd?.let { ad ->
        AndroidView(
            modifier = modifier.fillMaxWidth()
                .padding(vertical = dimensionResource(R.dimen.medium_padding)),
            factory = { context ->
                RecyclerView(context).apply {
                    layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                    setBackgroundColor(colors.surface.toArgb())
                    adapter = FeedAdAdapter(ad).apply {
                        eventListener = object : FeedAdEventListener {
                            override fun onAdClicked() {
                                Log.d("YandexAds", "Ad clicked")
                            }
                            override fun onImpression(impressionData: ImpressionData?) {
                                Log.d("YandexAds", "onImpression")
                            }
                        }
                    }
                }
            }
        )
    }
}
