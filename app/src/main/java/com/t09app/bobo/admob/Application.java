package com.t09app.bobo.admob;


import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize the Mobile Ads SDK
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                // You can add any additional initialization logic here
            }
        });
    }
}

