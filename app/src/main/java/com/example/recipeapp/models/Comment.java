package com.example.recipeapp.models;

import androidx.annotation.NonNull;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Comment")
public class Comment extends ParseObject {
    public static final String KEY_AUTHOR = "author";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_POST = "post";
    private static final String TAG = "Comment";


    public User getAuthor() {
        return new User(getParseUser(KEY_AUTHOR));
    }

    public void setAuthor(@NonNull final User author) {
        put(KEY_AUTHOR, author.getParseUser());
    }

    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public void setDescription(final String description) {
        put(KEY_DESCRIPTION, description);
    }

    public Post getPost() {
        return (Post) getParseObject(KEY_POST);
    }

    public void setPost(@NonNull final Post post) {
        put(KEY_POST, post);
    }
}
