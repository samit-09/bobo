package com.t09app.bobo.admob;


import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.t09app.bobo.utils.Config;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

public class AdRewarded {

    private static final String TAG = "AdRewarded";
    private static RewardedAd mRewardedAd;

    // Method to load a rewarded ad with the default ad unit ID
    public static void loadRewardedAd(Context context) {
        loadRewardedAd(context, Config.admob_rewarded_ad_unit_id);
    }

    // Method to check if a rewarded ad is loaded
    public static boolean isRewardedAdLoaded() {
        return mRewardedAd != null;
    }

    // Method to show the rewarded ad
    public static void showRewardedAd(Activity activity, final OnUserEarnedRewardListener listener) {
        if (mRewardedAd != null) {
            mRewardedAd.show(activity, new com.google.android.gms.ads.OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    // Callback when user earns a reward
                    listener.onUserEarnedReward(rewardItem);
                }
            });
        } else {
            Log.d(TAG, "The Rewarded ad wasn't ready yet.");
        }
    }

    // Method to load a rewarded ad with a specified ad unit ID
    private static void loadRewardedAd(Context context, String rewardedAdUnitId) {
        RewardedAd.load(context, rewardedAdUnitId, new AdRequest.Builder().build(), new RewardedAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                // Callback when the rewarded ad is loaded successfully
                mRewardedAd = rewardedAd;
                Log.d(TAG, "Rewarded ad loaded successfully");
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Callback when the rewarded ad fails to load
                mRewardedAd = null;
                Log.e(TAG, "Rewarded ad failed to load: " + loadAdError.getMessage());
            }
        });
    }

    // Interface for defining the callback when the user earns a reward
    public interface OnUserEarnedRewardListener {
        void onUserEarnedReward(RewardItem rewardItem);
    }
}
