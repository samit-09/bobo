package com.example.bobo.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.bobo.R;
import com.example.bobo.activity.FillProfileActivity;
import com.example.bobo.database.ProfileHelper;

public class ProfileFragment extends Fragment {

    private ImageView profileImageView;
    private TextView nameTextView, emailTextView, phoneTextView;
    private LinearLayout editProfileLayout;
    private ProfileHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        dbHelper = new ProfileHelper(getContext());

        profileImageView = view.findViewById(R.id.profile_image);
        nameTextView = view.findViewById(R.id.text_name);
        emailTextView = view.findViewById(R.id.text_email);
        phoneTextView = view.findViewById(R.id.text_phone);
        editProfileLayout = view.findViewById(R.id.edit_profile);

        loadProfile();

        editProfileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), FillProfileActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    private void loadProfile() {
        Cursor cursor = dbHelper.getAllProfiles();
        if (cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
            String phone = cursor.getString(cursor.getColumnIndexOrThrow("phone"));
            byte[] imageBytes = cursor.getBlob(cursor.getColumnIndexOrThrow("image"));

            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

            nameTextView.setText(name);
            emailTextView.setText(email);
            phoneTextView.setText(phone);
            profileImageView.setImageBitmap(bitmap);
        }
        cursor.close();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadProfile();
    }
}
