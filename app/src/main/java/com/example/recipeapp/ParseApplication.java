package com.example.recipeapp;

import android.app.Application;

import com.example.recipeapp.models.User;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {
    public void onCreate() {
        super.onCreate();
        ParseObject.registerSubclass(User.class);
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("3vCHYmPy6r9v1Yr2t7DrIKkQwZjeSmBePlEAYV5k")
                .clientKey("LjoqI1a25qeOyF8lW1oViF0Oql3AFJ1idPXOSDrD")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}