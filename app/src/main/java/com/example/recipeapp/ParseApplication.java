package com.example.recipeapp;

import android.app.Application;

import com.example.recipeapp.models.Comment;
import com.example.recipeapp.models.Ingredient;
import com.example.recipeapp.models.Post;
import com.example.recipeapp.models.Recipe;
import com.example.recipeapp.models.Review;
import com.example.recipeapp.models.User;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {
    public void onCreate() {
        super.onCreate();
        ParseObject.registerSubclass(User.class);
        ParseObject.registerSubclass(Recipe.class);
        ParseObject.registerSubclass(Post.class);
        ParseObject.registerSubclass(Ingredient.class);
        ParseObject.registerSubclass(Review.class);
        ParseObject.registerSubclass(Comment.class);
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("3vCHYmPy6r9v1Yr2t7DrIKkQwZjeSmBePlEAYV5k")
                .clientKey("LjoqI1a25qeOyF8lW1oViF0Oql3AFJ1idPXOSDrD")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}