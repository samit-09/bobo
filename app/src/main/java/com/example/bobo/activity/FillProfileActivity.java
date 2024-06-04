package com.example.bobo.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.bobo.R;
import com.example.bobo.database.ProfileHelper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FillProfileActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_PICK = 1;
    private static final int REQUEST_STORAGE_PERMISSION = 100;

    private ImageView profileImageView;
    private EditText nameEditText, emailEditText, phoneEditText;
    private Button continueButton;
    private ProfileHelper dbHelper;
    private Integer profileId = null;
    private Bitmap selectedImageBitmap = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_profile);

        setLightStatusBar();

        dbHelper = new ProfileHelper(this);

        profileImageView = findViewById(R.id.profile_image);
        nameEditText = findViewById(R.id.edit_text_name);
        emailEditText = findViewById(R.id.edit_text_email);
        phoneEditText = findViewById(R.id.edit_text_phone);
        continueButton = findViewById(R.id.button_continue);

        loadProfileData();

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermissionsAndPickImage();
            }
        });

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfileData();
            }
        });
    }

    private void setLightStatusBar() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));
    }

    private void loadProfileData() {
        Cursor cursor = dbHelper.getAllProfiles();
        if (cursor.moveToFirst()) {
            profileId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
            String phone = cursor.getString(cursor.getColumnIndexOrThrow("phone"));
            byte[] imageBytes = cursor.getBlob(cursor.getColumnIndexOrThrow("image"));

            if (imageBytes != null && imageBytes.length > 0) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                profileImageView.setImageBitmap(bitmap);
            }

            nameEditText.setText(name);
            emailEditText.setText(email);
            phoneEditText.setText(phone);
        }
        cursor.close();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void saveProfileData() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || selectedImageBitmap == null) {
            Toast.makeText(this, "Please fill all fields and select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("email", email);
        contentValues.put("phone", phone);
        byte[] imageBytes = getImageBytesFromBitmap(selectedImageBitmap);
        contentValues.put("image", imageBytes);

        boolean isSuccessful;
        boolean isUpdating = dbHelper.doesProfileExist();

        if (isUpdating) {
            isSuccessful = dbHelper.updateProfile(profileId, contentValues);
        } else {
            isSuccessful = dbHelper.insertProfile(contentValues);
        }

        if (isSuccessful) {
            if (isUpdating) {
                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                // Inflate the custom layout
                View customDialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog, null);

                // Create the Dialog
                Dialog dialog = new Dialog(this);
                dialog.setContentView(customDialogView);

                // Optional: Set background drawable
                dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.custom_dialog_background));

                // Set dialog dimensions and properties
                WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                dialog.getWindow().setAttributes(params);
                dialog.setCancelable(false); // Optional

                // Set margins for the dialog
                WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
                int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());
                layoutParams.width = getResources().getDisplayMetrics().widthPixels - 2 * margin;
                dialog.getWindow().setAttributes(layoutParams);

                // Show the custom dialog
                dialog.show();

                // Navigate to Home activity after 2 seconds
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        Intent intent = new Intent(FillProfileActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }, 2000); // 2000 milliseconds = 2 seconds
            }
        } else {
            Toast.makeText(this, "Failed to save profile", Toast.LENGTH_SHORT).show();
        }

    }

    private byte[] getImageBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private void requestPermissionsAndPickImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            String[] permissions = new String[]{
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO
            };
            requestPermissionsIfNeeded(permissions);
        } else {
            String[] permissions = new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE
            };
            requestPermissionsIfNeeded(permissions);
        }
    }

    private void requestPermissionsIfNeeded(String[] permissions) {
        List<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }

        if (permissionsToRequest.isEmpty()) {
            openGallery();
        } else {
            String[] permissionsArray = permissionsToRequest.toArray(new String[0]);
            boolean shouldShowRationale = false;

            for (String permission : permissionsArray) {
                if (shouldShowRequestPermissionRationale(permission)) {
                    shouldShowRationale = true;
                    break;
                }
            }

            if (shouldShowRationale) {
                new AlertDialog.Builder(this)
                        .setMessage("Please allow all permissions")
                        .setCancelable(false)
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                requestPermissionLauncher.launch(permissionsArray);
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .show();
            } else {
                requestPermissionLauncher.launch(permissionsArray);
            }
        }
    }

    private final ActivityResultLauncher<String[]> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), permissions -> {
                boolean allGranted = true;
                for (Boolean isGranted : permissions.values()) {
                    if (!isGranted) {
                        allGranted = false;
                        break;
                    }
                }
                if (allGranted) {
                    openGallery();
                } else {
                    Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
                }
            });

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                selectedImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                profileImageView.setImageBitmap(selectedImageBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
