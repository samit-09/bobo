package com.example.bobo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bobo.R;
import com.example.bobo.adapter.ChatAdapter;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ChatFragment extends Fragment {
    private GenerativeModelFutures model;
    private Executor executor = Executors.newSingleThreadExecutor();
    private EditText userInputEditText;
    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private TextView textViewEmptyConversation;
    private ImageButton actionButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        userInputEditText = view.findViewById(R.id.editTextUserInput);
        recyclerView = view.findViewById(R.id.recyclerViewChat);
        textViewEmptyConversation = view.findViewById(R.id.textViewEmpty);

        // Initialize the chat adapter and set it to the RecyclerView
        chatAdapter = new ChatAdapter(requireContext(), textViewEmptyConversation);
        recyclerView.setAdapter(chatAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Initialize Generative Model
        GenerativeModel gm = new GenerativeModel("gemini-pro", getString(R.string.gemini_api_key));
        model = GenerativeModelFutures.from(gm);

        actionButton = view.findViewById(R.id.actionButton);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userInput = userInputEditText.getText().toString().trim();
                if (!userInput.isEmpty()) {
                    sendMessage();
                } else {
                    Toast.makeText(requireContext(), "Please type something before sending", Toast.LENGTH_SHORT).show();
                }
            }
        });

        setLightStatusBar();

        return view;
    }

    private void setLightStatusBar() {
        // Set system UI visibility and status bar color for light theme
        requireActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        requireActivity().getWindow().setStatusBarColor(ContextCompat.getColor(requireContext(), R.color.white));
    }

    private void sendMessage() {
        String userInput = userInputEditText.getText().toString().trim();
        if (!userInput.isEmpty()) {
            chatAdapter.addMessage(userInput, false);
            userInputEditText.getText().clear();

            chatAdapter.addMessage("Please wait", true);

            Content content = new Content.Builder()
                    .addText(userInput)
                    .build();

            ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
            Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
                @Override
                public void onSuccess(GenerateContentResponse result) {
                    String botResponse = result.getText();
                    requireActivity().runOnUiThread(() -> {
                        chatAdapter.removeMessage("Please wait");
                        chatAdapter.addMessage(botResponse, true);
                        recyclerView.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
                    });
                }

                @Override
                public void onFailure(Throwable t) {
                    t.printStackTrace();
                    requireActivity().runOnUiThread(() -> {
                        chatAdapter.removeMessage("Please wait");
                        chatAdapter.addMessage("Error occurred. Please try again.", true);
                    });
                }
            }, executor);
        }
    }
}
