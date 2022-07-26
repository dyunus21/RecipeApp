package com.example.recipeapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.recipeapp.databinding.ItemProfilePostBinding;
import com.example.recipeapp.models.Post;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {
    private static final String TAG = "PostsAdapter";
    @NonNull private final Context context;
    @NonNull private final List<Post> postList;
    private ItemProfilePostBinding item_binding;

    public PostsAdapter(@NonNull final Context context, @NonNull final List<Post> posts) {
        this.context = context;
        this.postList = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent,final int viewType) {
        item_binding = ItemProfilePostBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(item_binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Post post = postList.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public void clear() {
        postList.clear();
        notifyDataSetChanged();
    }

    public void addAll(@NonNull final List<Post> posts) {
        postList.addAll(posts);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @NonNull private final ItemProfilePostBinding binding;

        public ViewHolder(@NonNull final ItemProfilePostBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }

        public void bind(@NonNull final Post post) {
            Glide.with(context).load(post.getImage().getUrl()).into(binding.ivImage);
        }
    }
}
