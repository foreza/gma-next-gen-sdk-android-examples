// Copyright 2025 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.example.snippets

import android.app.Activity
import android.util.Log
import android.view.ViewGroup
import com.google.android.libraries.ads.mobile.sdk.banner.BannerAd
import com.google.android.libraries.ads.mobile.sdk.common.AdLoadCallback
import com.google.android.libraries.ads.mobile.sdk.common.AdRequest
import com.google.android.libraries.ads.mobile.sdk.interstitial.InterstitialAd
import com.google.android.libraries.ads.mobile.sdk.nativead.MediaView
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAd
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAdView

/** Kotlin code snippets for the developer guide. */
class AdPlacementsSnippets {
  // [START load_interstitial]
  private fun loadInterstitial() {
    val adRequest = AdRequest.Builder(AD_UNIT_ID).setPlacementId(PLACEMENT_ID).build()
    InterstitialAd.load(
      adRequest,
      object : AdLoadCallback<InterstitialAd> {
        override fun onAdLoaded(ad: InterstitialAd) {
          Log.d(TAG, "Placement ID: " + ad.placementId)
        }
      },
    )
  }

  // [END load_interstitial]

  // [START show_interstitial]
  private fun showAd(activity: Activity, ad: InterstitialAd) {
    ad.placementId = PLACEMENT_ID
    ad.show(activity)
  }

  // [END show_interstitial]

  // [START show_banner]
  private fun showBannerView(ad: BannerAd, parentView: ViewGroup, activity: Activity) {
    ad.placementId = PLACEMENT_ID
    parentView.addView(ad.getView(activity))
  }

  // [END show_banner]

  // [START show_native]
  private fun showNativeAd(nativeAdView: NativeAdView, nativeAd: NativeAd, mediaView: MediaView) {
    nativeAd.placementId = PLACEMENT_ID
    nativeAdView.registerNativeAd(nativeAd, mediaView)
  }

  // [END show_native]

  private companion object {
    const val TAG = "AdPlacementsSnippets"
    const val PLACEMENT_ID = 2500718471
    const val AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"
  }
}
