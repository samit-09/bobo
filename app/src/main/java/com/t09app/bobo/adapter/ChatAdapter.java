package com.t09app.bobo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.t09app.bobo.R;
import com.t09app.bobo.database.ChatDatabaseHelper;
import com.t09app.bobo.model.ChatMessage;
import com.t09app.bobo.utils.TypeWriter;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private final List<ChatMessage> messages = new ArrayList<>();
    private final TextView textViewEmptyConversation; // TextView to display when conversation is empty
    private final ChatDatabaseHelper dbHelper; // Helper class for database operations
    private long chatId = -1; // ID of the chat associated with the adapter

    // Constructor
    public ChatAdapter(Context context, @Nullable TextView textViewEmptyConversation) {
        this.textViewEmptyConversation = textViewEmptyConversation;
        this.dbHelper = new ChatDatabaseHelper(context); // Initialize database helper
    }

    // Set the ID of the chat associated with the adapter
    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    // Add a new message to the list
    public void addMessage(String message, boolean isBot, boolean isFromDatabase) {
        if (!message.equals("Please wait")) {
            ChatMessage chatMessage = new ChatMessage(message, isBot); // Create new ChatMessage object
            messages.add(chatMessage); // Add message to the list
            notifyDataSetChanged(); // Notify RecyclerView about data change
            checkEmptyState(); // Check if conversation is empty and update UI
            // Save message to database if chatId is set and message is not from database
            if (chatId != -1 && !isFromDatabase) {
                dbHelper.saveMessage(chatId, chatMessage.getMessage(), chatMessage.isBot());
            }
        } else {
            // Handle "Please wait" message separately without saving to database
            ChatMessage chatMessage = new ChatMessage(message, isBot); // Create new ChatMessage object
            messages.add(chatMessage); // Add message to the list
            notifyDataSetChanged(); // Notify RecyclerView about data change
            checkEmptyState(); // Check if conversation is empty and update UI
        }
    }

    // Remove a message from the list
    public void removeMessage(String message) {
        for (ChatMessage chatMessage : messages) {
            if (chatMessage.getMessage().equals(message)) {
                messages.remove(chatMessage); // Remove message from the list
                break; // Exit loop after removing the message
            }
        }
        notifyDataSetChanged(); // Notify RecyclerView about data change
        checkEmptyState(); // Check if conversation is empty and update UI
    }

    // Check if conversation is empty and update UI accordingly
    private void checkEmptyState() {
        if (messages.isEmpty() && textViewEmptyConversation != null) {
            textViewEmptyConversation.setVisibility(View.VISIBLE); // Show empty conversation message
        } else {
            if (textViewEmptyConversation != null) {
                textViewEmptyConversation.setVisibility(View.GONE); // Hide empty conversation message
            }
        }
    }

    // Inflate the item layout and create the ViewHolder
    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout (item_message.xml) for RecyclerView
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new ChatViewHolder(view); // Return a new instance of ChatViewHolder
    }

    // Bind data to ViewHolder
    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatMessage message = messages.get(position); // Get message at the specified position

        // Check if this is the last message
        boolean isLastMessage = position == messages.size() - 1;
        holder.bind(message, isLastMessage); // Pass whether it's the last message
    }

    // Return the total number of items in the list
    @Override
    public int getItemCount() {
        return messages.size(); // Return size of messages list
    }

    // ViewHolder class that holds references to views for each item in RecyclerView
    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        TypeWriter textMessageBot;
        TextView textMessageUser;
        TextView textTimestampBot;
        TextView textTimestampUser;

        // Constructor that initializes the views
        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            textMessageBot = itemView.findViewById(R.id.textMessageBot);
            textMessageUser = itemView.findViewById(R.id.textMessageUser);
            textTimestampBot = itemView.findViewById(R.id.textTimestampBot);
            textTimestampUser = itemView.findViewById(R.id.textTimestampUser);

            textMessageBot.setOnLongClickListener(v -> {
                copyTextToClipboard(v.getContext(), textMessageBot.getText().toString());
                return true; // Consume the long click event
            });

            // Long click listener for copying user messages
            textMessageUser.setOnLongClickListener(v -> {
                copyTextToClipboard(v.getContext(), textMessageUser.getText().toString());
                return true; // Consume the long click event
            });

        }

        // Bind data to views
        public void bind(ChatMessage message, boolean isLastMessage) {
            textTimestampBot.setVisibility(View.GONE); // Hide bot timestamp by default
            textTimestampUser.setVisibility(View.GONE); // Hide user timestamp by default

            if (message.isBot()) { // Check if message is from bot
                textMessageBot.setVisibility(View.VISIBLE); // Show bot message TextView
                textMessageUser.setVisibility(View.GONE); // Hide user message TextView

                if (isLastMessage && !message.isAnimated()) { // Only animate if it's the last message and hasn't been animated
                    if (message.getMessage().equals("Please wait")){
                        textMessageBot.setCharacterDelay(9);
                        textMessageBot.animateText("Please Wait....");
                        message.setAnimated(true);
                    }else{
                        textMessageBot.setCharacterDelay(1);
                        textMessageBot.animateText(message.getMessage());
                        message.setAnimated(true);
                    }
                } else {
                    textMessageBot.setText(message.getMessage()); // Directly set text without animation
                }

                textTimestampBot.setText(message.getTimestamp()); // Set bot message timestamp
                textTimestampBot.setVisibility(View.VISIBLE); // Show bot timestamp TextView
            } else { // Message is from user
                textMessageUser.setText(message.getMessage()); // Set user message text
                textTimestampUser.setText(message.getTimestamp()); // Set user message timestamp
                textMessageUser.setVisibility(View.VISIBLE); // Show user message TextView
                textTimestampUser.setVisibility(View.VISIBLE); // Show user timestamp TextView
                textMessageBot.setVisibility(View.GONE); // Hide bot message TextView
            }
        }


        private void copyTextToClipboard(Context context, String text) {
            // Get clipboard manager service
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            // Create a new ClipData holding the copied text
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
            // Set the clip data to the clipboard
            clipboard.setPrimaryClip(clip);
            // Show a toast message to indicate that text is copied
            android.widget.Toast.makeText(context, "Text copied to clipboard", android.widget.Toast.LENGTH_SHORT).show();
        }



    }


}
