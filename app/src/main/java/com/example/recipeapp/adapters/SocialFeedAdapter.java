package com.example.recipeapp.adapters;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.recipeapp.R;
import com.example.recipeapp.databinding.ItemPostBinding;
import com.example.recipeapp.models.Post;
import com.example.recipeapp.models.User;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class SocialFeedAdapter extends RecyclerView.Adapter<SocialFeedAdapter.ViewHolder> {
    private static final String TAG = "SocialFeedAdapter";
    private final Context context;
    private final List<Post> postList;
    private ItemPostBinding item_binding;

    public SocialFeedAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        item_binding = ItemPostBinding.inflate(LayoutInflater.from(context), parent, false);
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

    public void addAll(List<Post> list) {
        postList.addAll(list);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemPostBinding binding;
        private Post currentPost;

        public ViewHolder(@NonNull ItemPostBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }

        public void bind(Post post) {
            currentPost = post;
            User user = new User(post.getAuthor());
            binding.tvUsername.setText("@" + post.getAuthor().getUsername());
            Glide.with(context).load(post.getImage().getUrl()).into(binding.ivImage);
            Glide.with(context).load(user.getProfileImage().getUrl()).circleCrop().into(binding.ivProfileImage);
            binding.tvTimestamp.setText(Post.calculateTimeAgo(post.getCreatedAt()));
            String sourceString = "<b>" + post.getAuthor().getUsername() + "</b> " + post.getDescription();
            binding.tvDescription.setText(Html.fromHtml(sourceString));
            // TODO: Go to User Profile page when clicked on user profile image

            List<ParseUser> likedBy = post.getLikedBy();
            binding.tvLikes.setText(likedBy.size() + " likes");
            if (post.isLikedbyCurrentUser(ParseUser.getCurrentUser())) {
                binding.ibHeart.setBackgroundResource(R.drawable.heart_filled);
            } else {
                binding.ibHeart.setBackgroundResource(R.drawable.heart);
            }

            binding.ibHeart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "Current User: " + ParseUser.getCurrentUser().getObjectId());
                    likePost();
                }
            });
        }

        private void likePost() {
            if (currentPost.isLikedbyCurrentUser(ParseUser.getCurrentUser())) {
                binding.ibHeart.setBackgroundResource(R.drawable.heart);
            } else {
                binding.ibHeart.setBackgroundResource(R.drawable.heart_filled);
            }
            currentPost.likePost(ParseUser.getCurrentUser());

            currentPost.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Log.e(TAG, "Error in liking post" + e);
                        return;
                    }
                    Log.i(TAG, "Successfully saved post");
                }
            });
            binding.tvLikes.setText(currentPost.getLikedBy().size() + " likes");
        }
    }

}
