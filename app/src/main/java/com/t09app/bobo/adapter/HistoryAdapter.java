package com.t09app.bobo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.t09app.bobo.R;
import com.t09app.bobo.model.ChatHistory;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private final Context context; // Context reference
    private final List<ChatHistory> chatHistoryList; // List to hold ChatHistory objects
    private final OnItemClickListener listener; // Listener for item click events

    // Constructor
    public HistoryAdapter(Context context, OnItemClickListener listener) {
        this.context = context;
        this.listener = listener;
        this.chatHistoryList = new ArrayList<>(); // Initialize empty list of ChatHistory
    }

    // Method to add a new chat title to the list
    public void addChatTitle(long chatId, String title) {
        chatHistoryList.add(new ChatHistory(chatId, title)); // Create new ChatHistory object and add to list
        notifyDataSetChanged(); // Notify RecyclerView about data change
    }

    // Create new ViewHolder
    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate item layout (item_chat.xml) for RecyclerView
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat, parent, false);
        return new HistoryViewHolder(view); // Return new instance of HistoryViewHolder
    }

    // Bind data to ViewHolder
    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        ChatHistory chatTitle = chatHistoryList.get(position); // Get ChatHistory object at position
        holder.bind(chatTitle, listener); // Bind data to ViewHolder

        if (position == chatHistoryList.size() - 1) {
            // Apply a bottom margin of 70dp to the last item
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();
            params.bottomMargin = (int) (70 * context.getResources().getDisplayMetrics().density); // Convert 40dp to pixels
            holder.itemView.setLayoutParams(params);
        }

    }

    // Return total number of items in the list
    @Override
    public int getItemCount() {
        return chatHistoryList.size(); // Return size of chatHistoryList
    }

    // Interface for item click events
    public interface OnItemClickListener {
        void onItemClick(long chatId); // Method called when item is clicked
    }

    // ViewHolder class for each item in RecyclerView
    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle; // TextView to display chat title

        // Constructor
        HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textChatTitle); // Initialize TextView from item_chat.xml
        }

        // Bind data to views
        void bind(ChatHistory chatTitle, OnItemClickListener listener) {
            textTitle.setText(chatTitle.getTitle()); // Set chat title text
            // Set click listener for the item
            itemView.setOnClickListener(v -> listener.onItemClick(chatTitle.getChatId()));
        }
    }
}
