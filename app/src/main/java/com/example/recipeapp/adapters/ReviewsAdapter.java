package com.example.recipeapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.recipeapp.databinding.ItemReviewBinding;
import com.example.recipeapp.models.Review;
import com.example.recipeapp.utilities.CurrentTimeProvider;
import com.example.recipeapp.utilities.TimeUtils;

import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {
    private static final String TAG = "ReviewsAdapter";
    @NonNull
    final public List<Review> reviews;
    @NonNull
    private final Context context;
    @NonNull
    private final TimeUtils timeUtils = new TimeUtils(new CurrentTimeProvider());
    private ItemReviewBinding item_binding;

    public ReviewsAdapter(@NonNull final Context context, @NonNull final List<Review> reviews) {
        this.context = context;
        this.reviews = reviews;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        item_binding = ItemReviewBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(item_binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final ReviewsAdapter.ViewHolder holder, final int position) {
        final Review review = reviews.get(position);
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

    public void addAll(@NonNull final List<Review> list) {
        reviews.addAll(list);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @NonNull
        public final ItemReviewBinding binding;

        public ViewHolder(@NonNull final ItemReviewBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }

        public void bind(@NonNull final Review review) {
            binding.tvUsername.setText("@" + review.getAuthor().getParseUser().getUsername());
            binding.tvTimestamp.setText(timeUtils.calculateTimeAgo(review.getCreatedAt()));
            binding.tvDescription.setText(review.getDescription());
            binding.rbRating.setRating(review.getRating());
            Glide.with(context).load(review.getAuthor().getProfileImage().getUrl()).circleCrop().into(binding.ivProfileImage);
        }

    }
}
