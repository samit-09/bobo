package com.t09app.bobo.admob;


import android.content.Context;
import android.view.View;

import com.t09app.bobo.utils.Config;
import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.nativead.NativeAd;

public class AdNative {

    // Method to load a native ad into a template view
    public static void loadNativeAd(Context context, TemplateView templateView) {

        // Create an ad loader with the specified ad unit ID
        AdLoader adLoader = new AdLoader.Builder(context, Config.admob_native_ad_unit_id)
                .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                    @Override
                    public void onNativeAdLoaded(NativeAd nativeAd) {
                        // Customize native ad template styles
                        NativeTemplateStyle styles = new NativeTemplateStyle.Builder().build();
                        // Apply styles to the template view
                        templateView.setStyles(styles);
                        // Set the loaded native ad to the template view
                        templateView.setNativeAd(nativeAd);
                        // Make the template view visible
                        templateView.setVisibility(View.VISIBLE);
                    }
                })
                .build();

        // Load the ad
        adLoader.loadAd(new AdRequest.Builder().build());
    }
}

