package com.example.recipeapp.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.recipeapp.activities.MainActivity;
import com.example.recipeapp.adapters.SocialFeedAdapter;
import com.example.recipeapp.databinding.FragmentSocialFeedBinding;
import com.example.recipeapp.models.EndlessRecyclerViewScrollListener;
import com.example.recipeapp.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class SocialFeedFragment extends Fragment {
    private static final String TAG = "SocialFragment";
    private FragmentSocialFeedBinding binding;
    private SocialFeedAdapter adapter;
    private List<Post> postList;
    private EndlessRecyclerViewScrollListener scrollListener;

    public SocialFeedFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        postList = new ArrayList<>();
        adapter = new SocialFeedAdapter(getContext(), postList);
        //:((MainActivity) getActivity()).getSupportActionBar().setTitle("Explore the Community!");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSocialFeedBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.rvPosts.setAdapter(adapter);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        binding.rvPosts.setLayoutManager(linearLayoutManager);

        binding.swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryPosts(null);
            }
        });
        queryPosts(null);
        binding.swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.i(TAG, "Since time: " + "post: " + postList.get(0).getDescription() + " " + postList.get(0).getCreatedAt());
                queryPosts(postList.get(0).getCreatedAt());
            }
        };
        binding.rvPosts.addOnScrollListener(scrollListener);
    }

    private void queryPosts(Date time) {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_AUTHOR);
        query.include(Post.KEY_IMAGE);
        query.include(Post.KEY_RECIPE_LINKED);
        query.include(Post.KEY_TITLE);
        query.setLimit(20);
        if (time != null) {
            Log.i(TAG, "Endless Scroll! on");
            query.whereLessThanOrEqualTo(Post.KEY_CREATED_AT, time);
        }
        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error in querying posts! ", e);
                    return;
                }
                adapter.clear();
                postList.addAll(objects);
                adapter.notifyDataSetChanged();
                binding.rvPosts.scrollToPosition(0);
                binding.swipeContainer.setRefreshing(false);
            }
        });
    }
}
