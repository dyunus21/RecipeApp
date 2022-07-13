package com.example.recipeapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.recipeapp.databinding.ItemReviewBinding;
import com.example.recipeapp.models.Review;

import java.util.ArrayList;
import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {
    private static final String TAG = "ReviewsAdapter";
    private final Context context;
    public List<Review> reviews;
    private ItemReviewBinding item_binding;

    public ReviewsAdapter(Context context) {
        this.reviews = new ArrayList<>();
        this.context = context;
    }

    public ReviewsAdapter(Context context, List<Review> reviews) {
        this.context = context;
        this.reviews = reviews;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        item_binding = ItemReviewBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(item_binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewsAdapter.ViewHolder holder, int position) {
        Review review = reviews.get(position);
        holder.bind(review);
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public void clear() {
        reviews.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Review> list) {
        reviews.addAll(list);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ItemReviewBinding binding;
        private Review review;

        public ViewHolder(@NonNull ItemReviewBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }

        public void bind(Review review) {
            binding.tvUsername.setText(review.getAuthor().getParseUser().getUsername());
            binding.tvTimestamp.setText(Review.calculateTimeAgo(review.getCreatedAt()));
            binding.tvDescription.setText(review.getDescription());
            binding.rbRating.setRating(review.getRating());
            Glide.with(context).load(review.getAuthor().getProfileImage().getUrl()).circleCrop().into(binding.ivProfileImage);
        }

    }
}
