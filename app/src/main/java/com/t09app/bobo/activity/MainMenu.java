package com.t09app.bobo.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;


import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.t09app.bobo.R;
import com.t09app.bobo.admob.AdBanner;

import java.util.Objects;

public class MainMenu extends AppCompatActivity {

    LinearLayout watchVideoBtn, referFriendBtn, boboChatBtn;
    ImageView top_wallet_icon, spin_wheel_btn, top_coin_icon;
    String clicked = "";
    private RewardedAd mRewardedAd, chatRewardedAd;
    public int referCount;
    public int tempCoin;
    public int chatCount;
//    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://bobo-chatbot-default-rtdb.firebaseio.com/");
    FirebaseAuth mAuth;
    public String simplified_email_string, dbCoin;
    boolean is_ad_loading_failed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_menu);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        watchVideoBtn = findViewById(R.id.watchVideoBtn);
        referFriendBtn = findViewById(R.id.referFriendBtn);
        boboChatBtn = findViewById(R.id.boboChatBtn);
        top_wallet_icon = findViewById(R.id.top_wallet_icon);
        spin_wheel_btn = findViewById(R.id.spin_wheel_btn);
        top_coin_icon = findViewById(R.id.top_coin_icon);


//        setupAdmobBannerAds();

        setStatusBarColor();
        applySavedDarkModeSetting();
        setBottomNavigationView();

        setupListener();
        handleNoInternetProblem();
        setupAnimation();
//        firebaseAuthSetup();



    }


    // Method to set the status bar color based on the theme
    private void setStatusBarColor() {
        // Get the current night mode
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        // Check if it's in dark mode
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            setDarkStatusBar(); // Set the status bar color for dark theme
        } else {
            setLightStatusBar(); // Set the status bar color for light theme
        }
    }

    // Method to set the status bar for dark theme
    private void setDarkStatusBar() {
        // Set system UI visibility and status bar color for dark theme
        getWindow().getDecorView().setSystemUiVisibility(0);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.black));
    }

    // Method to set the status bar for light theme
    private void setLightStatusBar() {
        // Set system UI visibility and status bar color for light theme
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));
    }

    // Method to apply the saved dark mode setting
    private void applySavedDarkModeSetting() {
        // Retrieve the saved dark mode setting from SharedPreferences
        SharedPreferences darkPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean isDarkModeEnabled = darkPreferences.getBoolean("darkMode", false);

        // Apply the dark mode setting to the app's theme
        if (isDarkModeEnabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    public void setBottomNavigationView() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set the listener for navigation item selection
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId(); // Get the ID of the selected item

                if (itemId == R.id.wallet) {
                    // Navigate to WalletActivity
                    startActivity(new Intent(MainMenu.this, WalletActivity.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left); // No animation
                    return true;

                } else if (itemId == R.id.profile) {
                    // Navigate to ProfileActivity
                    startActivity(new Intent(MainMenu.this, ProfileActivity.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left); // No animation
                    return true;

                } else if (itemId == R.id.home) {
                    // Stay on HomeActivity
                    return true;

                } else {
                    return false; // Handle unexpected cases
                }
            }
        });
    }

    public void handleNoInternetProblem(){

        if (!isNetworkAvailable(this)){
            new AlertDialog.Builder(this)
                    .setTitle("No Internet!")
                    .setMessage("You have no internet connection! Please use this app with active internet connection")
                    .setIcon(R.drawable.no_internet_icon)
                    .setCancelable(false)
                    .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(Intent.ACTION_MAIN);
                            intent.addCategory(Intent.CATEGORY_HOME);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    })
                    .show();
        }

    }

    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public void setupAnimation(){

        ObjectAnimator rotateAnimator = ObjectAnimator.ofFloat(spin_wheel_btn, "rotation", 0f, 360f);
        rotateAnimator.setDuration(2200); // Duration of one full rotation in milliseconds
        rotateAnimator.setInterpolator(new LinearInterpolator()); // Linear Interpolator for smooth continuous rotation
        rotateAnimator.setRepeatCount(ObjectAnimator.INFINITE); // Infinite repeat count to keep rotating
        rotateAnimator.start();


        ObjectAnimator floatAnimator = ObjectAnimator.ofFloat(top_wallet_icon, "translationY", 0f, -15f);
        floatAnimator.setDuration(1000); // Duration of one floating cycle in milliseconds
        floatAnimator.setRepeatCount(ObjectAnimator.INFINITE); // Repeat infinitely
        floatAnimator.setRepeatMode(ObjectAnimator.REVERSE); // Reverse animation to create up-and-down effect
        floatAnimator.setInterpolator(new AccelerateDecelerateInterpolator()); // Smooth acceleration and deceleration
        floatAnimator.start();


        ObjectAnimator flipToMid = ObjectAnimator.ofFloat(top_coin_icon, "rotationY", 0f, 90f);
        flipToMid.setDuration(300); // Duration for half flip
        flipToMid.setRepeatCount(1);
        flipToMid.setRepeatMode(ObjectAnimator.REVERSE);

        // ObjectAnimator to flip the image view back from 90 degrees to 0 degrees
        ObjectAnimator flipFromMid = ObjectAnimator.ofFloat(top_coin_icon, "rotationY", 90f, 0f);
        flipFromMid.setDuration(300); // Duration for the second half flip

        // AnimatorSet to play both flips in sequence
        AnimatorSet flipAnim = new AnimatorSet();
        flipAnim.playSequentially(flipToMid, flipFromMid);

        // Start the animation
        flipAnim.start();

    }

    public void setupListener(){


        boboChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(MainMenu.this, HomeActivity.class));

            }
        });


        top_wallet_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainMenu.this, WalletActivity.class));
            }
        });


        spin_wheel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainMenu.this, SpinWheelActivity.class));
            }
        });


    }

    public void setupAdmobBannerAds(){

        LinearLayout adContainerView = findViewById(R.id.layBottomBanner); // Replace with your layout ID
        // Show banner ad
        AdBanner.showBannerAd(MainMenu.this, adContainerView);

    }

//    public void firebaseAuthSetup(){
//
//        mAuth = FirebaseAuth.getInstance();
//
//        StringBuilder simplified_email = new StringBuilder();
//
//        if (mAuth.getCurrentUser() != null){
//            String email = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
//            if (email != null) {
//                for(int i=0; i < email.length(); i++) {
//
//                    if (email.substring(i,i+1).contains(".")){
//
//                        simplified_email.append("_");
//
//                    }
//                    else{
//                        simplified_email.append(email.charAt(i));
//                    }
//
//                }
//            }
//
//            simplified_email_string = String.valueOf(simplified_email);
//        }
//
////        if (mAuth.getCurrentUser()!= null){
////
////            databaseReference.child("users").addValueEventListener(new ValueEventListener() {
////                @Override
////                public void onDataChange(@NonNull DataSnapshot snapshot) {
////
////                    if (snapshot.hasChild(simplified_email_string)){
////                        dbCoin = snapshot.child(simplified_email_string).child("coin").getValue(String.class);
////                    }
////
////                }
////
////                @Override
////                public void onCancelled(@NonNull DatabaseError error) {
////
////                }
////            });
////
////        }
//
//
//    }
//


}