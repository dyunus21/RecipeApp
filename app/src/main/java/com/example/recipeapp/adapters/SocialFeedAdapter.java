package com.example.recipeapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
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
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.recipeapp.R;
import com.example.recipeapp.databinding.ItemPostBinding;
import com.example.recipeapp.fragments.RecipeDetailsFragment;
import com.example.recipeapp.models.Comment;
import com.example.recipeapp.models.Post;
import com.example.recipeapp.models.Recipe;
import com.example.recipeapp.models.User;
import com.example.recipeapp.utilities.CurrentTimeProvider;
import com.example.recipeapp.utilities.TimeUtils;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;
import java.util.Objects;

public class SocialFeedAdapter extends RecyclerView.Adapter<SocialFeedAdapter.ViewHolder> {
    private static final String TAG = "SocialFeedAdapter";
    @NonNull
    private final Context context;
    @NonNull
    private final List<Post> postList;
    @NonNull
    private final User CURRENT_USER = new User(ParseUser.getCurrentUser());
    @NonNull
    private final TimeUtils timeUtils = new TimeUtils(new CurrentTimeProvider());
    private ItemPostBinding item_binding;

    public SocialFeedAdapter(@NonNull final Context context, @NonNull final List<Post> postList) {
        this.context = context;
        this.postList = postList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        item_binding = ItemPostBinding.inflate(LayoutInflater.from(context), parent, false);
        User.getUser(CURRENT_USER);
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

    public void addAll(@NonNull final List<Post> list) {
        postList.addAll(list);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemPostBinding binding;
        private Post currentPost;
        @NonNull
        private final CommentsAdapter commentsAdapter;


        @SuppressLint("ClickableViewAccessibility")
        public ViewHolder(@NonNull final ItemPostBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
            commentsAdapter = new CommentsAdapter(context);
        }


        @SuppressLint("ClickableViewAccessibility")
        public void bind(@NonNull final Post post) {
            currentPost = post;
            final User user = post.getAuthor();
            binding.tvUsername.setText("@" + user.getParseUser().getUsername());
            Glide.with(context).load(post.getImage().getUrl()).into(binding.ivImage);
            Glide.with(context).load(user.getProfileImage().getUrl()).circleCrop().into(binding.ivProfileImage);
            binding.tvTimestamp.setText(timeUtils.calculateTimeAgo(post.getCreatedAt()));
            binding.tvTitle.setText(post.getTitle());
            final String sourceString = "<b>" + user.getParseUser().getUsername() + "</b> " + post.getDescription();
            binding.tvDescription.setText(Html.fromHtml(sourceString));

            binding.ivProfileImage.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putParcelable("User", post.getAuthor());
                Log.i(TAG, "Bundling " + post.getAuthor().getParseUser().getUsername());
                Navigation.findNavController(v).navigate(R.id.profileFragment,bundle);
            });

            final List<ParseUser> likedBy = post.getLikedBy();
            binding.tvLikes.setText(likedBy.size() + " likes");
            if (post.isLikedbyCurrentUser(ParseUser.getCurrentUser())) {
                binding.ibHeart.setBackgroundResource(R.drawable.ic_baseline_favorite_24);
            } else {
                binding.ibHeart.setBackgroundResource(R.drawable.ic_baseline_favorite_border_24);
            }

            binding.ibHeart.setOnClickListener(v -> likePost());

            Glide.with(context).load(CURRENT_USER.getProfileImage().getUrl()).circleCrop().into(binding.ivCurrentUserProfileImage);
            binding.rvComments.setLayoutManager(new LinearLayoutManager(context));
            binding.rvComments.setAdapter(commentsAdapter);
            binding.btnPostComment.setOnClickListener(v -> postComment());
            refreshComments();

            final GestureDetector gestureDetector = new GestureDetector(context.getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onDoubleTap(@NonNull final MotionEvent e) {
                    Log.i(TAG, "double tapped post: " + currentPost.getTitle());
                    Toast.makeText(context, "Double tapped post: " + currentPost.getTitle(), Toast.LENGTH_SHORT).show();
                    likePost();
                    return false;
                }

                @Override
                public boolean onDown(@NonNull final MotionEvent e) {
                    return true;
                }
            });
            binding.ivImage.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));
            if (post.getRecipeLinked() != null) {
                binding.btnGoToRecipe.setVisibility(View.VISIBLE);
            }
            binding.btnGoToRecipe.setOnClickListener(v -> {
                final Bundle bundle = new Bundle();
                bundle.putParcelable(Recipe.class.getSimpleName(), post.getRecipeLinked());
                final RecipeDetailsFragment recipeDetailsFragment = new RecipeDetailsFragment();
                recipeDetailsFragment.setArguments(bundle);
                Navigation.findNavController(v).navigate(R.id.recipeDetailsFragment, bundle);
            });

            binding.tvPostType.setText(post.getType());
            // TODO: Change tv color based on type

        }

        private void likePost() {
            if (currentPost.isLikedbyCurrentUser(ParseUser.getCurrentUser())) {
                binding.ibHeart.setBackgroundResource(R.drawable.ic_baseline_favorite_border_24);
            } else {
                binding.ibHeart.setBackgroundResource(R.drawable.ic_baseline_favorite_24);
            }
            currentPost.likePost(ParseUser.getCurrentUser());

            currentPost.saveInBackground(e -> {
                if (e != null) {
                    Log.e(TAG, "Error in liking post" + e);
                    return;
                }
                Log.i(TAG, "Successfully saved post");
            });
            binding.tvLikes.setText(currentPost.getLikedBy().size() + " likes");
        }

        private void refreshComments() {
            final ParseQuery<Comment> query = ParseQuery.getQuery("Comment");
            query.whereEqualTo(Comment.KEY_POST, currentPost);
            query.orderByDescending(Comment.KEY_CREATED_AT);
            query.include(Comment.KEY_AUTHOR);
            query.include(Comment.KEY_DESCRIPTION);
            query.findInBackground((objects, e) -> {
                if (e != null) {
                    Log.e(TAG, "Error in fetching comments");
                    return;
                }
                commentsAdapter.clear();
                commentsAdapter.comments.addAll(objects);
                commentsAdapter.notifyDataSetChanged();
            });
        }

        private void postComment() {
            final String body = Objects.requireNonNull(binding.etComment.getText()).toString();
            final Comment comment = new Comment();
            comment.setAuthor(CURRENT_USER);
            comment.setDescription(body);
            comment.setPost(currentPost);
            comment.saveInBackground(e -> {
                if (e != null) {
                    Log.e(TAG, "Error with saving comment! " + e.getMessage());
                    return;
                }
                Log.i(TAG, "Successfully added comment");
                binding.etComment.setText("");
                refreshComments();

            });
        }
    }

}
