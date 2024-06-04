package com.example.bobo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bobo.R;
import com.example.bobo.model.ChatMessage;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<ChatMessage> messages = new ArrayList<>();
    private final TextView textViewEmptyConversation; // TextView to show when conversation is empty

    // Constructor
    public ChatAdapter(Context context, @Nullable TextView textViewEmptyConversation) {
        this.textViewEmptyConversation = textViewEmptyConversation;
    }

    // Add a new message to the list
    public void addMessage(String message, boolean isBot) {
        ChatMessage chatMessage = new ChatMessage(message, isBot);
        messages.add(chatMessage);
        notifyDataSetChanged(); // Notify RecyclerView about data change
        checkEmptyState(); // Check if conversation is empty
    }

    // Remove a message from the list
    public void removeMessage(String message) {
        for (ChatMessage chatMessage : messages) {
            if (chatMessage.getMessage().equals(message)) {
                messages.remove(chatMessage);
                break;
            }
        }
        notifyDataSetChanged(); // Notify RecyclerView about data change
        checkEmptyState(); // Check if conversation is empty
    }

    // Check if conversation is empty and update UI accordingly
    private void checkEmptyState() {
        if (messages.isEmpty() && textViewEmptyConversation != null) {
            textViewEmptyConversation.setVisibility(View.VISIBLE);
        } else {
            if (textViewEmptyConversation != null) {
                textViewEmptyConversation.setVisibility(View.GONE);
            }
        }
    }

    // Inflate the item layout and create the ViewHolder
    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new ChatViewHolder(view);
    }

    // Bind data to ViewHolder
    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatMessage message = messages.get(position);
        holder.bind(message);
    }

    // Return the total number of items in the list
    @Override
    public int getItemCount() {
        return messages.size();
    }

    // ViewHolder class
    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView textMessageBot;
        TextView textMessageUser;
        TextView textTimestampBot;
        TextView textTimestampUser;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            textMessageBot = itemView.findViewById(R.id.textMessageBot);
            textMessageUser = itemView.findViewById(R.id.textMessageUser);
            textTimestampBot = itemView.findViewById(R.id.textTimestampBot);
            textTimestampUser = itemView.findViewById(R.id.textTimestampUser);
        }

        // Bind data to views
        public void bind(ChatMessage message) {
            textTimestampBot.setVisibility(View.GONE);
            textTimestampUser.setVisibility(View.GONE);

            if (message.isBot()) {
                if (message.getMessage().equals("Please wait")) {
                    textMessageBot.setText("Please wait...");
                    textMessageBot.setVisibility(View.VISIBLE);
                    textMessageUser.setVisibility(View.GONE);
                } else {
                    textMessageBot.setText(message.getMessage());
                    textTimestampBot.setText(message.getTimestamp());
                    textMessageBot.setVisibility(View.VISIBLE);
                    textTimestampBot.setVisibility(View.VISIBLE);
                    textMessageUser.setVisibility(View.GONE);
                }
            } else {
                textMessageUser.setText(message.getMessage());
                textTimestampUser.setText(message.getTimestamp());
                textMessageUser.setVisibility(View.VISIBLE);
                textTimestampUser.setVisibility(View.VISIBLE);
                textMessageBot.setVisibility(View.GONE);
            }
        }
    }
}
