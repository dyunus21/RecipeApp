package com.example.recipeapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.recipeapp.R;
import com.example.recipeapp.databinding.ItemPostBinding;
import com.example.recipeapp.models.Comment;
import com.example.recipeapp.models.Post;
import com.example.recipeapp.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class SocialFeedAdapter extends RecyclerView.Adapter<SocialFeedAdapter.ViewHolder> {
    private static final String TAG = "SocialFeedAdapter";
    private final Context context;
    private final List<Post> postList;
    private final User CURRENT_USER = new User(ParseUser.getCurrentUser());
    private ItemPostBinding item_binding;

    public SocialFeedAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        item_binding = ItemPostBinding.inflate(LayoutInflater.from(context), parent, false);
        User.getUser(CURRENT_USER);
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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener {
        private final ItemPostBinding binding;
        private Post currentPost;
        private CommentsAdapter commentsAdapter;

        public ViewHolder(@NonNull ItemPostBinding itemView) {
            super(itemView.getRoot());
            itemView.getRoot().setOnTouchListener(this);
            this.binding = itemView;
        }


        public void bind(Post post) {
            currentPost = post;
            User user = post.getAuthor();
            binding.tvUsername.setText("@" + user.getParseUser().getUsername());
            Glide.with(context).load(post.getImage().getUrl()).into(binding.ivImage);
            Glide.with(context).load(user.getProfileImage().getUrl()).circleCrop().into(binding.ivProfileImage);
            binding.tvTimestamp.setText(Post.calculateTimeAgo(post.getCreatedAt()));
            String sourceString = "<b>" + user.getParseUser().getUsername() + "</b> " + post.getDescription();
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
                    likePost();
                }
            });

            Glide.with(context).load(CURRENT_USER.getProfileImage().getUrl()).circleCrop().into(binding.ivCurrentUserProfileImage);
            binding.rvComments.setLayoutManager(new LinearLayoutManager(context));
            commentsAdapter = new CommentsAdapter(context);
            binding.rvComments.setAdapter(commentsAdapter);
            binding.btnPostComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    postComment();
                }
            });
            refreshComments();
            //TODO: Collapse comments when exceeding certain limit
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

        private void refreshComments() {
            ParseQuery<Comment> query = ParseQuery.getQuery("Comment");
            query.whereEqualTo(Comment.KEY_POST, currentPost);
            query.orderByDescending(Comment.KEY_CREATED_AT);
            query.include(Comment.KEY_AUTHOR);
            query.include(Comment.KEY_DESCRIPTION);
            query.findInBackground(new FindCallback<Comment>() {
                @Override
                public void done(List<Comment> objects, ParseException e) {
                    if (e != null) {
                        Log.e(TAG, "Error in fetching comments");
                        return;
                    }
                    commentsAdapter.clear();
                    commentsAdapter.comments.addAll(objects);
                    commentsAdapter.notifyDataSetChanged();
                }
            });
        }

        private void postComment() {
            String body = binding.etComment.getText().toString();
            Comment comment = new Comment();
            comment.setAuthor(CURRENT_USER);
            comment.setDescription(body);
            comment.setPost(currentPost);
            comment.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Log.e(TAG, "Error with saving comment! " + e.getMessage());
                        return;
                    }
                    Log.i(TAG, "Successfully added comment");
                    binding.etComment.setText("");
                    refreshComments();

                }
            });
        }

        // TODO: Blocker: Gesture Detector detects Long Press instead of Double Tap
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            Log.i(TAG, "onTouch post: " + currentPost.getTitle());

            GestureDetector gestureDetector = new GestureDetector(v.getContext(), new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    Log.i(TAG, "double tapped post: " + currentPost.getTitle());
                    Toast.makeText(context, "Double tapped post: " + currentPost.getTitle(), Toast.LENGTH_SHORT).show();
                    // likePost();
                    return false;
                }

                @Override
                public boolean onDown(MotionEvent e) {
                    return true;
                }
            });
            gestureDetector.onTouchEvent(event);
            return false;
        }

    }

}
