package com.example.recipeapp.models;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ParseClassName("Post")
public class Post extends ParseObject {
    public static final String TAG = "Post";
    public static final String KEY_AUTHOR = "author";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_TITLE = "title";
    public static final String KEY_LIKED_BY = "likedby";
    public static final String KEY_COMMENTS = "comments";

    public static String calculateTimeAgo(Date createdAt) {

        int SECOND_MILLIS = 1000;
        int MINUTE_MILLIS = 60 * SECOND_MILLIS;
        int HOUR_MILLIS = 60 * MINUTE_MILLIS;
        int DAY_MILLIS = 24 * HOUR_MILLIS;

        try {
            createdAt.getTime();
            long time = createdAt.getTime();
            long now = System.currentTimeMillis();

            final long diff = now - time;
            if (diff < MINUTE_MILLIS) {
                return "just now";
            } else if (diff < 2 * MINUTE_MILLIS) {
                return "a minute ago";
            } else if (diff < 50 * MINUTE_MILLIS) {
                return diff / MINUTE_MILLIS + " m";
            } else if (diff < 90 * MINUTE_MILLIS) {
                return "an hour ago";
            } else if (diff < 24 * HOUR_MILLIS) {
                return diff / HOUR_MILLIS + " h";
            } else if (diff < 48 * HOUR_MILLIS) {
                return "yesterday";
            } else {
                return diff / DAY_MILLIS + " d";
            }
        } catch (Exception e) {
            Log.e(TAG, "Error: getRelativeTimeAgo failed", e);
            e.printStackTrace();
        }

        return "";
    }

    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public void setDescription(String description) {
        put(KEY_DESCRIPTION, description);
    }

    public String getTitle() {
        return getString(KEY_TITLE);
    }

    public void setTitle(String title) {
        put(KEY_TITLE, title);
    }

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile image) {
        put(KEY_IMAGE, image);
    }

    public ParseUser getAuthor() {
        return getParseUser(KEY_AUTHOR);
    }

    public void setAuthor(User author) {
        put(KEY_AUTHOR, author.getParseUser());
    }

    public List<ParseUser> getLikedBy() {
        List<ParseUser> likedBy = getList(KEY_LIKED_BY);
        if (likedBy == null)
            return new ArrayList<>();
        return likedBy;
    }

    public void setLikedBy(List<ParseUser> likedBy) {
        put(KEY_LIKED_BY, likedBy);
    }

    public List<Comment> getComments() {
        List<Comment> comments = getList(KEY_COMMENTS);
        if (comments == null)
            return new ArrayList<>();
        return comments;
    }

    public void setComments(List<Comment> comments) {
        put(KEY_COMMENTS, comments);
    }


}
