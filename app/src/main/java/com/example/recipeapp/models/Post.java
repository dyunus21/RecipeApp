package com.example.recipeapp.models;

import android.util.Log;

import androidx.annotation.NonNull;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

@ParseClassName("Post")
public class Post extends ParseObject {
    public static final String TAG = "Post";
    public static final String KEY_AUTHOR = "author";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_TITLE = "title";
    public static final String KEY_LIKED_BY = "likedBy";
    public static final String KEY_TYPE = "type";
    public static final String KEY_RECIPE_LINKED = "recipeLinked";

    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public void setDescription(final String description) {
        put(KEY_DESCRIPTION, description);
    }

    public String getTitle() {
        return getString(KEY_TITLE);
    }

    public void setTitle(final String title) {
        put(KEY_TITLE, title);
    }

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    public void setImage(final ParseFile image) {
        put(KEY_IMAGE, image);
    }


    public User getAuthor() {
        return new User(getParseUser(KEY_AUTHOR));
    }

    public void setAuthor(@NonNull final User author) {
        put(KEY_AUTHOR, author.getParseUser());
    }

    public Recipe getRecipeLinked() {
        return (Recipe) getParseObject(KEY_RECIPE_LINKED);
    }

    public void setRecipeLinked(@NonNull final Recipe recipe) {
        put(KEY_RECIPE_LINKED, recipe);
    }

    public List<ParseUser> getLikedBy() {
        List<ParseUser> likedBy = getList(KEY_LIKED_BY);
        if (likedBy == null)
            return new ArrayList<>();
        return likedBy;
    }

    public void setLikedBy(@NonNull final List<ParseUser> likedBy) {
        put(KEY_LIKED_BY, likedBy);
    }

    public boolean isLikedbyCurrentUser(@NonNull final ParseUser currentUser) {
        for (ParseUser user : getLikedBy()) {
            if (currentUser.hasSameId(user)) {
                Log.i(TAG, "Post is already liked by " + currentUser.getUsername());
                return true;
            }
        }
        Log.i(TAG, "Post has not been liked by " + currentUser.getUsername());
        return false;
    }

    public void likePost(@NonNull final ParseUser currentUser) {
        List<ParseUser> likedBy = getLikedBy();
        for (int i = 0; i < likedBy.size(); i++) {
            if (likedBy.get(i).hasSameId(currentUser)) {
                likedBy.remove(i);
                Log.i(TAG, "Size: " + likedBy.size());
                setLikedBy(likedBy);
                return;
            }
        }
        likedBy.add(currentUser);
        setLikedBy(likedBy);
    }

    public String getType() {
        return getString(KEY_TYPE);
    }

    public void setType(final String type) {
        put(KEY_TYPE, type);
    }


}
