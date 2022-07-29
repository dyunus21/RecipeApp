package com.example.recipeapp.models.parse;

import androidx.annotation.NonNull;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.Objects;

@ParseClassName("Comment")
public class Comment extends ParseObject {
    public static final String KEY_AUTHOR = "author";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_POST = "post";
    private static final String TAG = "Comment";


    public User getAuthor() {
        return new User(Objects.requireNonNull(getParseUser(KEY_AUTHOR)));
    }

    public void setAuthor(@NonNull final User author) {
        put(KEY_AUTHOR, author.getParseUser());
    }

    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public void setDescription(@NonNull final String description) {
        put(KEY_DESCRIPTION, description);
    }

    public Post getPost() {
        return (Post) getParseObject(KEY_POST);
    }

    public void setPost(@NonNull final Post post) {
        put(KEY_POST, post);
    }
}
