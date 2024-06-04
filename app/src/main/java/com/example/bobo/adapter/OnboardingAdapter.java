package com.example.bobo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bobo.R;

public class OnboardingAdapter extends RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder> {

    private int[] images;
    private String[] titles;
    public OnboardingAdapter(int[] images, String[] titles) {
        this.images = images;
        this.titles = titles;
    }

    @NonNull
    @Override
    public OnboardingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.slide_layout, parent, false);
        return new OnboardingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OnboardingViewHolder holder, int position) {
        holder.imageView.setImageResource(images[position]);
        holder.title.setText(titles[position]);

        if (position == getItemCount() - 1) {
            holder.getStartedButton.setVisibility(View.VISIBLE);
        } else {
            holder.getStartedButton.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return titles.length;
    }

    static class OnboardingViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView title;
        Button getStartedButton;

        OnboardingViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.slide_image);
            title = itemView.findViewById(R.id.slide_title);
            getStartedButton = itemView.findViewById(R.id.getStartedButton);
        }
    }
}

