package com.example.recipeapp.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.recipeapp.databinding.ItemCommentBinding;
import com.example.recipeapp.models.Comment;
import com.example.recipeapp.utilities.CurrentTimeProvider;
import com.example.recipeapp.utilities.TimeUtils;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {
    private static final String TAG = "CommentsAdapter";
    public List<Comment> comments;
    private final Context context;
    private ItemCommentBinding item_binding;
    private final TimeUtils timeUtils = new TimeUtils(new CurrentTimeProvider());

    public CommentsAdapter(Context context) {
        this.comments = new ArrayList<>();
        this.context = context;
    }

    public CommentsAdapter(Context context, List<Comment> comments) {
        this.context = context;
        this.comments = comments;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        item_binding = ItemCommentBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(item_binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsAdapter.ViewHolder holder, int position) {
        Comment comment = comments.get(position);
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

    public void addAll(List<Comment> list) {
        comments.addAll(list);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ItemCommentBinding binding;
        private Comment comment;

        public ViewHolder(@NonNull ItemCommentBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }

        public void bind(Comment comment) {
            String sourceString = "<b>" + comment.getAuthor().getParseUser().getUsername() + "</b> " + comment.getDescription();
            binding.tvBody.setText(Html.fromHtml(sourceString));
            Glide.with(context).load(comment.getAuthor().getProfileImage().getUrl()).circleCrop().into(binding.ivProfileImage);
            binding.tvTimestamp.setText(timeUtils.calculateTimeAgo(comment.getCreatedAt()));
        }

    }
}
