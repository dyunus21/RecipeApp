package com.example.recipeapp.socialFeed.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.recipeapp.R;
import com.example.recipeapp.databinding.ItemCommentBinding;
import com.example.recipeapp.models.parse.Comment;
import com.example.recipeapp.utilities.CurrentTimeProvider;
import com.example.recipeapp.utilities.TimeUtils;

import java.util.ArrayList;
import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {
    private static final String TAG = "CommentsAdapter";
    @NonNull
    public final List<Comment> comments;
    @NonNull
    private final Context context;
    private final TimeUtils timeUtils = new TimeUtils(new CurrentTimeProvider());
    private ItemCommentBinding item_binding;

    public CommentsAdapter(@NonNull final Context context) {
        this.comments = new ArrayList<>();
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        item_binding = ItemCommentBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(item_binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final CommentsAdapter.ViewHolder holder, final int position) {
        final Comment comment = comments.get(position);
        holder.bind(comment);
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public void clear() {
        comments.clear();
        notifyDataSetChanged();
    }

    public void addAll(@NonNull final List<Comment> list) {
        comments.addAll(list);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final ItemCommentBinding binding;

        public ViewHolder(@NonNull final ItemCommentBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }

        public void bind(@NonNull final Comment comment) {
            final String sourceString = "<b>" + comment.getAuthor().getParseUser().getUsername() + "</b> " + comment.getDescription();
            binding.tvBody.setText(Html.fromHtml(sourceString));
            Glide.with(context).load(comment.getAuthor().getProfileImage().getUrl()).placeholder(R.drawable.ic_baseline_account_circle_24).circleCrop().into(binding.ivProfileImage);
            binding.tvTimestamp.setText(timeUtils.calculateTimeAgo(comment.getCreatedAt()));
        }

    }
}
