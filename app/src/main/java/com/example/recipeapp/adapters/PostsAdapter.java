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
    private final Context context;
    private final List<Post> postList;
    private ItemProfilePostBinding item_binding;

    public PostsAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.postList = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        item_binding = ItemProfilePostBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(item_binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
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

    public void addAll(List<Post> posts) {
        postList.addAll(posts);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemProfilePostBinding binding;

        public ViewHolder(@NonNull ItemProfilePostBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }

        public void bind(Post post) {
            Glide.with(context).load(post.getImage().getUrl()).into(binding.ivImage);
        }
    }
}
