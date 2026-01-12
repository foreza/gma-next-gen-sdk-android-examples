/*
 * Copyright 2025 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.nextgenexample.rewarded;

import static com.example.nextgenexample.Constant.TAG;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.nextgenexample.AdFragment;
import com.example.nextgenexample.R;
import com.example.nextgenexample.databinding.FragmentRewardedBinding;
import com.google.android.libraries.ads.mobile.sdk.common.AdRequest;
import com.google.android.libraries.ads.mobile.sdk.common.AdValue;
import com.google.android.libraries.ads.mobile.sdk.common.LoadAdError;
import com.google.android.libraries.ads.mobile.sdk.common.PreloadCallback;
import com.google.android.libraries.ads.mobile.sdk.common.PreloadConfiguration;
import com.google.android.libraries.ads.mobile.sdk.common.ResponseInfo;
import com.google.android.libraries.ads.mobile.sdk.rewarded.RewardedAd;
import com.google.android.libraries.ads.mobile.sdk.rewarded.RewardedAdEventCallback;
import com.google.android.libraries.ads.mobile.sdk.rewarded.RewardedAdPreloader;

/** A [Fragment] subclass that preloads rewarded ads. */
public class RewardedFragment extends AdFragment<FragmentRewardedBinding> {

  // Sample rewarded ad unit ID.
  // TODO: Replace this test ad unit ID with your own ad unit ID.
  private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917";
  private static final long COUNTDOWN_INTERVAL = 50L;
  private static final long GAME_LENGTH_MILLISECONDS = 5000L;
  private static final int GAME_OVER_REWARD = 1;
  private CountDownTimer countDownTimer;
  private boolean gamePaused;
  private boolean gameOver;
  private long timeLeftMillis;
  private int coinCount;

  @Override
  protected BindingInflater<FragmentRewardedBinding> getBindingInflater() {
    return FragmentRewardedBinding::inflate;
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    // Start preloading.
    startPreloading();
    startGame();

    // Initialize the UI.
    binding.playAgainButton.setOnClickListener(
        sender -> {
          startGame();
          updateUI();
        });

    binding.watchVideoButton.setOnClickListener(
        sender -> {
          showRewardedAd();
          updateUI();
        });
    updateUI();
  }

  private void startPreloading() {
    PreloadCallback preloadCallback =
        // [Important] Do not call start() or pollAd() within the callback.
        new PreloadCallback() {
          @Override
          public void onAdFailedToPreload(
              @NonNull String preloadId, @NonNull LoadAdError loadAdError) {
            Log.i(TAG, "Rewarded ad failed to preload with error: " + loadAdError.getMessage());
          }

          @Override
          public void onAdsExhausted(@NonNull String preloadId) {
            Log.i(TAG, "Rewarded ads exhausted.");
            showToast("Rewarded ads exhausted.");
            updateUI();
          }

          @Override
          public void onAdPreloaded(@NonNull String preloadId, @NonNull ResponseInfo responseInfo) {
            Log.i(TAG, "Rewarded ad preloaded.");
            showToast("Rewarded ad preloaded.");
            updateUI();
          }
        };
    AdRequest adRequest = new AdRequest.Builder(AD_UNIT_ID).build();
    PreloadConfiguration preloadConfig = new PreloadConfiguration(adRequest);
    RewardedAdPreloader.start(AD_UNIT_ID, preloadConfig, preloadCallback);
  }

  private void showRewardedAd() {
    // Polling returns the next available ad and loads another ad in the background.
    RewardedAd ad = RewardedAdPreloader.pollAd(AD_UNIT_ID);
    if (ad == null) {
      Log.i(TAG, "No preloaded rewarded ads available.");
      return;
    }

    // Interact with the ad object as needed.
    Log.d(TAG, "Rewarded ad response info: " + ad.getResponseInfo());
    ad.setAdEventCallback(
        new RewardedAdEventCallback() {
          @Override
          public void onAdImpression() {
            Log.d(TAG, "Rewarded ad recorded an impression.");
          }

          @Override
          public void onAdPaid(@NonNull AdValue value) {
            Log.d(
                TAG,
                String.format(
                    "Rewarded ad onAdPaid: %d %s",
                    value.getValueMicros(), value.getCurrencyCode()));
          }

          @Override
          public void onAdShowedFullScreenContent() {
            binding.watchVideoButton.setVisibility(View.INVISIBLE);
          }
        });

    ad.show(
        requireActivity(),
        rewardItem -> {
          // Handle the reward.
          int rewardAmount = rewardItem.getAmount();
          String rewardType = rewardItem.getType();
          Log.d(TAG, String.format("User earned reward of %d %s.", rewardAmount, rewardType));
        });
    updateUI();
  }

  public synchronized void updateUI() {
    runOnUiThread(
        () -> {
          boolean isAdAvailable = RewardedAdPreloader.isAdAvailable(AD_UNIT_ID);
          int statusResId = isAdAvailable ? R.string.available : R.string.exhausted;
          binding.txtStatus.setText(statusResId);
          binding.coins.setText(getString(R.string.coins, coinCount));
        });
  }

  private void addCoins(int coins) {
    coinCount += coins;
    requireActivity()
        .runOnUiThread(() -> binding.coins.setText(getString(R.string.coins, coinCount)));
  }

  /**
   * Create the game timer, which counts down to the end of the level and shows the "Play again"
   * button.
   */
  private void createTimer(long milliseconds) {
    if (countDownTimer != null) {
      countDownTimer.cancel();
    }

    countDownTimer =
        new CountDownTimer(milliseconds, COUNTDOWN_INTERVAL) {
          @Override
          public void onTick(long millisUntilFinished) {
            timeLeftMillis = millisUntilFinished;
            // Display countdown start from 5.0 seconds.
            int seconds = (int) (millisUntilFinished / 1000);
            if (millisUntilFinished % 1000 != 0) {
              seconds++;
            }

            binding.timer.setText(
                getResources().getQuantityString(R.plurals.seconds_left, seconds, seconds));
          }

          @Override
          public void onFinish() {
            gameOver = true;
            binding.watchVideoButton.setVisibility(View.VISIBLE);
            binding.timer.setText(getString(R.string.you_lose));
            binding.playAgainButton.setVisibility(View.VISIBLE);

            addCoins(GAME_OVER_REWARD);
          }
        };

    countDownTimer.start();
  }

  /** Set the game back to "start". */
  private void startGame() {
    binding.playAgainButton.setVisibility(View.INVISIBLE);
    binding.watchVideoButton.setVisibility(View.INVISIBLE);
    createTimer(GAME_LENGTH_MILLISECONDS);
    gamePaused = false;
    gameOver = false;
  }

  /** Cancel the timer. */
  private void pauseGame() {
    if (gameOver || gamePaused) {
      return;
    }

    if (countDownTimer != null) {
      countDownTimer.cancel();
    }
    gamePaused = true;
  }

  /** Create timer with the active time remaining. */
  private void resumeGame() {
    if (gameOver || !gamePaused) {
      return;
    }

    createTimer(timeLeftMillis);
    gamePaused = false;
  }

  @Override
  public void onResume() {
    super.onResume();
    resumeGame();
  }

  @Override
  public void onPause() {
    super.onPause();
    pauseGame();
  }
}
