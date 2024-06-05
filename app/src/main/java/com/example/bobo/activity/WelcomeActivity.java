package com.example.bobo.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.bobo.R;
import com.example.bobo.database.ProfileHelper;
import com.example.bobo.fragment.ChatFragment;

public class WelcomeActivity extends AppCompatActivity {

    private ProfileHelper dbHelper;
    private TextView profileNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome);

        setLightStatusBar();

        profileNameTextView = findViewById(R.id.profile_name);

        dbHelper = new ProfileHelper(this);

        loadProfileName();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setLightStatusBar() {
        // Set system UI visibility and status bar color for light theme
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));
    }

    private void loadProfileName() {
        Cursor cursor = dbHelper.getAllProfiles();
        if (cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            profileNameTextView.setText("Welcome, " + name + "!👋");
        }
        cursor.close();
    }

    public void onGetStartedButtonClick(View view) {
        openHomeActivity();

    }

    private void openHomeActivity() {
        startActivity(new Intent(WelcomeActivity.this, HomeActivity.class));
        finish();
    }
}
