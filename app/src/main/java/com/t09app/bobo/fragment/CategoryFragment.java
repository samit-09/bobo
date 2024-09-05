package com.t09app.bobo.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.t09app.bobo.R;
import com.t09app.bobo.activity.ChatActivity;
import com.t09app.bobo.adapter.CategoryAdapter;
import com.t09app.bobo.admob.AdBanner;

import java.util.Arrays;
import java.util.List;

public class CategoryFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_category, container, false);

        // Initialize RecyclerView from layout
        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerView);

        // Create a GridLayoutManager with 2 columns
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);

        LinearLayout adContainerView = rootView.findViewById(R.id.adContainerView); // Replace with your layout ID
        // Show banner ad
        AdBanner.showBannerAd(getContext(), adContainerView);

        // Sample data for titles, images, and descriptions
        List<String> titles = Arrays.asList("Articles", "Translate", "Essay", "Summarize", "Story", "Code");
        List<Integer> images = Arrays.asList(R.drawable.articles, R.drawable.translate, R.drawable.essay, R.drawable.summarize, R.drawable.story, R.drawable.code);
        List<String> descriptions = Arrays.asList(
                "Explore a variety of well-researched articles covering topics from science to lifestyle.",
                "Effortlessly translate text between multiple languages with accuracy and ease.",
                "Craft compelling essays on diverse subjects with structured arguments and thorough research.",
                "Condense lengthy texts into concise summaries without losing critical information.",
                "Create imaginative and engaging stories that captivate readers with vibrant characters and plots.",
                "Generate well-structured code snippets in multiple programming languages.");

        // Create an instance of CategoryAdapter with the data and an item click listener
        CategoryAdapter adapter = new CategoryAdapter(titles, images, descriptions, new CategoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String description) {
                // Handle item click: create an intent to start ChatActivity with description data
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("description", description); // Pass description as an extra to ChatActivity
                startActivity(intent); // Start the activity
            }
        });

        recyclerView.setAdapter(adapter); // Set the adapter to the RecyclerView

        return rootView; // Return the prepared view for display
    }
}
