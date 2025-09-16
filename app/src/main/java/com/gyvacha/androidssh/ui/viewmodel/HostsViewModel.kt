package com.gyvacha.androidssh.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gyvacha.androidssh.BuildConfig
import com.gyvacha.androidssh.domain.model.Host
import com.gyvacha.androidssh.domain.usecase.DeleteHostUseCase
import com.gyvacha.androidssh.domain.usecase.GetHostsUseCase
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.nativeads.NativeAd
import com.yandex.mobile.ads.nativeads.NativeAdLoadListener
import com.yandex.mobile.ads.nativeads.NativeAdLoader
import com.yandex.mobile.ads.nativeads.NativeAdRequestConfiguration
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HostsViewModel @Inject constructor(
    private val deleteHostUseCase: DeleteHostUseCase,
    getHostsUseCase: GetHostsUseCase,
    @param:ApplicationContext private val context: Context
) : ViewModel() {

    val hosts: StateFlow<List<Host>> = getHostsUseCase()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _nativeAd = MutableStateFlow<NativeAd?>(null)
    val nativeAd: StateFlow<NativeAd?> = _nativeAd

    fun deleteHost(host: Host) {
        viewModelScope.launch {
            deleteHostUseCase(host)
        }
    }

    fun loadNativeAd() {
        val loader = NativeAdLoader(context)
        loader.setNativeAdLoadListener(object : NativeAdLoadListener {
            override fun onAdLoaded(nativeAd: NativeAd) {
                _nativeAd.value = nativeAd
            }

            override fun onAdFailedToLoad(error: AdRequestError) {
                Log.e("YandexAds", "Ad failed: $error")
                _nativeAd.value = null
            }
        })

        val requestConfig = NativeAdRequestConfiguration.Builder(BuildConfig.YANDEX_AD_UNIT_ID)
            .build()
        loader.loadAd(requestConfig)
    }
}
