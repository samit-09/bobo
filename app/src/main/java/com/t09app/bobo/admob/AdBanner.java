package com.t09app.bobo.admob;


import android.content.Context;
import android.widget.LinearLayout;

import com.t09app.bobo.utils.Config;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

// Class responsible for displaying banner ads
public class AdBanner {

    // Method to show a banner ad
    public static void showBannerAd(Context context, LinearLayout adContainerView) {
        // Create an AdView and set its ad size.
        AdView adView = new AdView(context);
        adView.setAdSize(AdSize.BANNER); // Adjust the size as needed (e.g., AdSize.FULL_BANNER)

        // Set the ad unit ID.
        adView.setAdUnitId(Config.admob_banner_ad_unit_id);

        // Clear existing views and add the AdView to the view hierarchy.
        adContainerView.removeAllViews();
        adContainerView.addView(adView);

        // Create an AdRequest.
        AdRequest adRequest = new AdRequest.Builder().build();

        // Load the ad.
        adView.loadAd(adRequest);
    }
}

