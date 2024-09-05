package com.t09app.bobo.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.t09app.bobo.R;
import com.t09app.bobo.activity.DarkModeActivity;
import com.t09app.bobo.activity.HomeActivity;
import com.t09app.bobo.activity.MainMenu;
import com.t09app.bobo.admob.AdInterstitial;
import com.t09app.bobo.database.ChatDatabaseHelper;
import com.t09app.bobo.database.ProfileHelper;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    private ImageView profileImageView;
    private TextView nameTextView, emailTextView, phoneTextView;
    private LinearLayout layout_darkmode_profile;
    private LinearLayout deleteHistoryLayout;
    private ProfileHelper dbHelper;
    private AdInterstitial adInterstitial;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize adInterstitial
        adInterstitial = new AdInterstitial(getContext());

        // Initialize views and components
        dbHelper = new ProfileHelper(getContext());
        setStatusBarColor();

        profileImageView = view.findViewById(R.id.profile_image);
        nameTextView = view.findViewById(R.id.text_name);
        emailTextView = view.findViewById(R.id.text_email);
        phoneTextView = view.findViewById(R.id.text_phone);
        LinearLayout editProfileLayout = view.findViewById(R.id.edit_profile);
        layout_darkmode_profile = view.findViewById(R.id.layout_darkmode_profile);
        deleteHistoryLayout = view.findViewById(R.id.delete_history_settings); // Initialize the new layout

        // Load user profile data
        loadProfile();

        // Set click listeners for various settings
        editProfileLayout.setOnClickListener(this);
        layout_darkmode_profile.setOnClickListener(this);
        deleteHistoryLayout.setOnClickListener(this); // Set click listener for delete history

        // Handle back icon click to finish the activity
        ImageView backIcon = view.findViewById(R.id.backIcon);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() instanceof HomeActivity) {
                    ((HomeActivity) getActivity()).loadFragment(new HistoryFragment());
                }
            }
        });

        return view;
    }

    // Method to set the status bar color based on the theme
    private void setStatusBarColor() {
        // Get the current night mode
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        // Check if it's in dark mode
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            // Set the status bar color for dark theme
            setDarkStatusBar();
        } else {
            // Set the status bar color for light theme
            setLightStatusBar();
        }
    }

    // Method to set the status bar for dark theme
    private void setDarkStatusBar() {
        // Set system UI visibility and status bar color for dark theme
        requireActivity().getWindow().getDecorView().setSystemUiVisibility(0);
        requireActivity().getWindow().setStatusBarColor(ContextCompat.getColor(getContext(), R.color.black));
    }

    // Method to set the status bar for light theme
    private void setLightStatusBar() {
        // Set system UI visibility and status bar color for light theme
        requireActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        requireActivity().getWindow().setStatusBarColor(ContextCompat.getColor(getContext(), R.color.white));
    }

    // Method to load user profile data from the database
    private void loadProfile() {
        Cursor cursor = dbHelper.getAllProfiles();
        if (cursor.moveToFirst()) {
            // Retrieve profile data from the cursor
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
            String phone = cursor.getString(cursor.getColumnIndexOrThrow("phone"));
            byte[] imageBytes = cursor.getBlob(cursor.getColumnIndexOrThrow("image"));
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

            // Set profile data to respective views
            nameTextView.setText(name);
            emailTextView.setText(email);
            phoneTextView.setText(phone);
            profileImageView.setImageBitmap(bitmap);
        }
        cursor.close();
    }

    @Override
    public void onClick(View v) {
        // Handle click events for various settings/options
        if (v.getId() == R.id.edit_profile) {
            // Open Edit Profile activity
            Intent editProfileIntent = new Intent(getContext(), MainMenu.class);
            startActivity(editProfileIntent);
            // Show interstitial ad
            adInterstitial.showInterstitialAd(getContext());
            // Apply custom transition animation
            requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } else if (v.getId() == R.id.layout_darkmode_profile) {
            // Open Dark Mode Settings activity
            Intent settingsProfileIntent = new Intent(getContext(), DarkModeActivity.class);
            startActivity(settingsProfileIntent);
            // Show interstitial ad
            adInterstitial.showInterstitialAd(getContext());
            // Apply custom transition animation
            requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }else if (v.getId() == R.id.delete_history_settings) {
            // Show confirmation dialog before deleting chat history
            showDeleteHistoryDialog();
        }
    }


    // Method to show confirmation dialog before deleting chat history
    private void showDeleteHistoryDialog() {
        // Inflate custom layout for delete history dialog
        View customDialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_delete_history, null);

        // Create Dialog instance
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(customDialogView);

        // Optional: Set background drawable for dialog
        dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.custom_dialog_background));

        // Set dialog dimensions and properties
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes(params);
        dialog.setCancelable(false); // Optional: Prevent dialog from being dismissed by tapping outside

        // Set margins for the dialog
        WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
        int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());
        layoutParams.width = getResources().getDisplayMetrics().widthPixels - 2 * margin;
        dialog.getWindow().setAttributes(layoutParams);

        // Find and set click listeners for Yes and No buttons
        Button btnYes = customDialogView.findViewById(R.id.btnYes);
        Button btnNo = customDialogView.findViewById(R.id.btnNo);

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform delete chat history operation
                deleteChatHistory();
                dialog.dismiss(); // Dismiss the dialog after operation is performed
            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss(); // Dismiss the dialog if user cancels the operation
            }
        });

        // Show the custom dialog
        dialog.show();
    }

    // Method to delete chat history from the database
    private void deleteChatHistory() {
        ChatDatabaseHelper chatDbHelper = new ChatDatabaseHelper(getContext());
        SQLiteDatabase db = chatDbHelper.getWritableDatabase();

        // Begin transaction for atomicity
        db.beginTransaction();
        try {
            // Delete all chat messages
            db.delete(ChatDatabaseHelper.TABLE_MESSAGES, null, null);

            // Delete all chat titles
            db.delete(ChatDatabaseHelper.TABLE_CHAT_TITLE, null, null);

            // Mark transaction as successful
            db.setTransactionSuccessful();

            // Show toast message indicating successful deletion
            Toast.makeText(getContext(), "Chat history deleted", Toast.LENGTH_SHORT).show();
            // Show interstitial ad after chat history is cleared
            adInterstitial.showInterstitialAd(getContext());
        } finally {
            // End transaction
            db.endTransaction();
            db.close(); // Close the database connection
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh profile data when fragment resumes
        loadProfile();
    }
}
