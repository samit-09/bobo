package com.t09app.bobo.activity;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.t09app.bobo.R;
import com.t09app.bobo.adapter.ChatAdapter;
import com.t09app.bobo.admob.AdRewarded;
import com.t09app.bobo.database.ChatDatabaseHelper;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ChatActivity extends AppCompatActivity {
    private GenerativeModelFutures model;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private EditText userInputEditText;
    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private ChatDatabaseHelper dbHelper;
    private long chatId = -1;
    private ImageButton actionButton;
    private boolean isVoiceMode = true;
    private static final int REQUEST_CODE_VOICE_INPUT = 1000;

    private static final String[] LANGUAGES = {
            "en-US", "es-ES", "fr-FR", "de-DE", "it-IT", "pt-PT", "nl-NL", "ru-RU", "ja-JP",
            "ko-KR", "zh-CN", "zh-TW", "ar-SA", "hi-IN", "bn-BD", "tr-TR", "vi-VN", "pl-PL",
            "sv-SE", "no-NO", "da-DK", "fi-FI", "cs-CZ", "sk-SK", "ro-RO", "hu-HU", "el-GR",
            "he-IL", "th-TH", "id-ID", "ms-MY", "fil-PH", "uk-UA", "bg-BG", "hr-HR", "lt-LT",
            "lv-LV", "et-EE", "te-IN"  // Added Telugu
    };

    private static final String[] LANGUAGES_DISPLAY = {
            "English", "Spanish", "French", "German", "Italian", "Portuguese", "Dutch", "Russian", "Japanese",
            "Korean", "Chinese (Simplified)", "Chinese (Traditional)", "Arabic", "Hindi", "Bengali", "Turkish", "Vietnamese", "Polish",
            "Swedish", "Norwegian", "Danish", "Finnish", "Czech", "Slovak", "Romanian", "Hungarian", "Greek",
            "Hebrew", "Thai", "Indonesian", "Malay", "Filipino", "Ukrainian", "Bulgarian", "Croatian", "Lithuanian",
            "Latvian", "Estonian", "Telugu"  // Added Telugu
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        setStatusBarColor();

        userInputEditText = findViewById(R.id.editTextUserInput);
        recyclerView = findViewById(R.id.recyclerViewChat);
        TextView textViewEmptyConversation = findViewById(R.id.textViewEmpty);
        dbHelper = new ChatDatabaseHelper(this);

        chatAdapter = new ChatAdapter(this, textViewEmptyConversation);
        recyclerView.setAdapter(chatAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        GenerativeModel gm = new GenerativeModel("gemini-pro", getString(R.string.gemini_api_key));
        model = GenerativeModelFutures.from(gm);

        recyclerView.post(() -> recyclerView.scrollToPosition(chatAdapter.getItemCount() - 1));

        if (getIntent().hasExtra("description")) {
            String description = getIntent().getStringExtra("description");
            userInputEditText.setText(description);
        }

        if (getIntent().getExtras() != null) {
            chatId = getIntent().getLongExtra("chatId", -1);
            if (chatId != -1) {
                chatAdapter.setChatId(chatId);
                loadMessages(chatId);
            }
        }

        actionButton = findViewById(R.id.actionButton);
        updateActionButtonIcon();

        userInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateActionButtonIcon();
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isVoiceMode) {
                    requestAudioPermission();
                } else {
                    String userInput = userInputEditText.getText().toString().trim();
                    if (!userInput.isEmpty()) {
                        sendMessage();
                        recyclerView.post(() -> recyclerView.scrollToPosition(chatAdapter.getItemCount() - 1));
                        if (ClickCounter.increment() % 3 == 0) {
                            showRewardedAd();
                        }
                    } else {
                        Toast.makeText(ChatActivity.this, "Please type something before sending", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        ImageView backIcon = findViewById(R.id.backIcon);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void updateActionButtonIcon() {
        String userInput = userInputEditText.getText().toString().trim();
        if (userInput.isEmpty()) {
            actionButton.setImageResource(R.drawable.microphone); // Replace with actual microphone icon resource
            isVoiceMode = true;
        } else {
            actionButton.setImageResource(R.drawable.send); // Replace with actual send icon resource
            isVoiceMode = false;
        }
    }

    private void showLanguageSelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Language");

        builder.setItems(LANGUAGES_DISPLAY, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selectedLanguage = LANGUAGES[which];
                startVoiceInput(selectedLanguage);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void startVoiceInput(String languageCode) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, languageCode);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...");

        try {
            startActivityForResult(intent, REQUEST_CODE_VOICE_INPUT);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Google Voice Input is not supported on your device", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_VOICE_INPUT && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (result != null && !result.isEmpty()) {
                userInputEditText.setText(result.get(0));
            }
        }
    }

    private void requestAudioPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_CODE_VOICE_INPUT);
        } else {
            showLanguageSelectionDialog();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_VOICE_INPUT) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showLanguageSelectionDialog();
            } else {
                Toast.makeText(this, "Permission denied to record audio", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setStatusBarColor() {
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            setDarkStatusBar();
        } else {
            setLightStatusBar();
        }
    }

    private void setDarkStatusBar() {
        getWindow().getDecorView().setSystemUiVisibility(0);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.black));
    }

    private void setLightStatusBar() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));
    }

    private void loadMessages(long chatId) {
        Cursor cursor = dbHelper.getMessagesByChatId(chatId);
        while (cursor.moveToNext()) {
            String message = cursor.getString(cursor.getColumnIndexOrThrow(ChatDatabaseHelper.COLUMN_MESSAGE));
            boolean isBot = cursor.getInt(cursor.getColumnIndexOrThrow(ChatDatabaseHelper.COLUMN_IS_BOT)) == 1;
            chatAdapter.addMessage(message, isBot, true);
        }
        cursor.close();
    }

    private void sendMessage() {
        String userInput = userInputEditText.getText().toString().trim();
        if (!userInput.isEmpty()) {
            if (chatId == -1) {
                chatId = dbHelper.saveChatTitle(userInput);
                chatAdapter.setChatId(chatId);
            }

            chatAdapter.addMessage(userInput, false, false);
            userInputEditText.getText().clear();

            chatAdapter.addMessage("Please wait", true, true);

            Content content = new Content.Builder().addText(userInput).build();
            ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
            Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
                @Override
                public void onSuccess(GenerateContentResponse result) {
                    String botResponse = result.getText();
                    runOnUiThread(() -> {
                        chatAdapter.removeMessage("Please wait");
                        assert botResponse != null;
                        chatAdapter.addMessage(botResponse, true, false);
                        recyclerView.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
                    });
                }

                @Override
                public void onFailure(@NonNull Throwable t) {
                    t.printStackTrace();
                    runOnUiThread(() -> {
                        chatAdapter.removeMessage("Please wait");
                        chatAdapter.addMessage("Error occurred. Please try again.", true, false);
                    });
                }
            }, executor);
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void showRewardedAd() {
        AdRewarded.loadRewardedAd(this);
        if (AdRewarded.isRewardedAdLoaded()) {
            AdRewarded.showRewardedAd(this, new AdRewarded.OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(RewardItem rewardItem) {
                    // Handle reward earned by the user
                }
            });
        } else {
            // Handle case where the ad is not loaded yet
        }
    }

    public static class ClickCounter {
        private static int count = 0;

        public static int increment() {
            return ++count;
        }
    }
}
