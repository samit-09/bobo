package com.example.bobo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.bobo.activity.FillProfileActivity;
import com.example.bobo.activity.WelcomeActivity;
import com.example.bobo.adapter.OnboardingAdapter;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

public class MainActivity extends AppCompatActivity {

    private static final String PREF_NAME = "OnboardingPrefs";
    private static final String PREF_ONBOARDING_COMPLETED = "OnboardingCompleted";

    private ViewPager2 viewPager;
    private Button nextButton;
    private OnboardingAdapter adapter;
    private WormDotsIndicator dotsIndicator;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setLightStatusBar();

        SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        boolean onboardingCompleted = preferences.getBoolean(PREF_ONBOARDING_COMPLETED, false);

        if (onboardingCompleted) {
            // Skip onboarding and go directly to WelcomeActivity
            startActivity(new Intent(this, WelcomeActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewPager);
        nextButton = findViewById(R.id.nextButton);
        dotsIndicator = findViewById(R.id.dotsIndicator);

        int[] images = {R.drawable.robot_face, R.drawable.bot_amico, R.drawable.chat_bot};
        String[] titles = {"Welcome To Bobo,\na great friend to\nchat with you", "If you are confused\nabout what to do,\njust open Bobo", "Bobo will be ready\nto chat and make\nyou happy"};

        adapter = new OnboardingAdapter(images, titles);
        viewPager.setAdapter(adapter);

        dotsIndicator.setViewPager2(viewPager);

        nextButton.setOnClickListener(v -> {
            int currentItem = viewPager.getCurrentItem();
            if (currentItem < adapter.getItemCount() - 1) {
                viewPager.setCurrentItem(currentItem + 1);
            }
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == adapter.getItemCount() - 1) {
                    nextButton.setVisibility(View.GONE);
                } else {
                    nextButton.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    // Method to set the status bar for light theme
    private void setLightStatusBar() {
        // Set system UI visibility and status bar color for light theme
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));
    }


    public void onGetStartedButtonClick(View view) {
        SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(PREF_ONBOARDING_COMPLETED, true);
        editor.apply();

        Intent intent = new Intent(this, FillProfileActivity.class);
        startActivity(intent);
        finish();
    }
}
