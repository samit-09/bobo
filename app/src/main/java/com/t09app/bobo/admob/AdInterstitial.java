package com.t09app.bobo.admob;


import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.t09app.bobo.utils.Config;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class AdInterstitial {

    private static final String TAG = "AdInterstitial";
    private InterstitialAd mInterstitialAd;

    // Constructor to initialize the interstitial ad
    public AdInterstitial(Context context) {
        loadInterstitialAd(context);
    }

    // Method to load the interstitial ad
    private void loadInterstitialAd(Context context) {
        // Create an ad request
        AdRequest adRequest = new AdRequest.Builder().build();

        // Load the interstitial ad
        InterstitialAd.load(context, Config.admob_interstitial_ad_unit_id, adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // Ad loaded successfully, assign it to the member variable
                        mInterstitialAd = interstitialAd;
                        // Set full screen content callback to handle ad dismissals
                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Reload the ad after it's dismissed
                                loadInterstitialAd(context);
                            }
                        });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Ad failed to load, log the error
                        mInterstitialAd = null;
                        Log.e(TAG, "Interstitial ad failed to load: " + loadAdError.getMessage());
                    }
                });
    }

    // Method to show the interstitial ad
    public void showInterstitialAd(Context context) {
        // Check if the interstitial ad is ready
        if (mInterstitialAd != null) {
            // Show the ad
            mInterstitialAd.show((Activity) context);
        } else {
            // Log if the ad wasn't ready yet
            Log.d(TAG, "The interstitial ad wasn't ready yet.");
        }
    }
}

