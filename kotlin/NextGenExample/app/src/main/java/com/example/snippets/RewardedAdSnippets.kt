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

import com.google.android.libraries.ads.mobile.sdk.common.AdRequest
import com.google.android.libraries.ads.mobile.sdk.common.PreloadConfiguration
import com.google.android.libraries.ads.mobile.sdk.rewarded.RewardedAdPreloader

/** Kotlin code snippets for the developer guide. */
private class RewardedAdSnippets {

  // [START start_preload]
  private fun startPreloading(adUnitId: String) {
    val adRequest = AdRequest.Builder(adUnitId).build()
    val preloadConfig = PreloadConfiguration(adRequest)
    RewardedAdPreloader.start(adUnitId, preloadConfig)
  }

  // [END start_preload]

  private fun pollAd(adUnitId: String) {
    // [START poll_ad]
    // Polling returns the next available ad and loads another ad in the background.
    val ad = RewardedAdPreloader.pollAd(adUnitId)
    // [END poll_ad]
  }
}
