package com.example.recipeapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.recipeapp.activities.MainActivity;
import com.example.recipeapp.databinding.FragmentUploadBinding;
import com.example.recipeapp.models.ModalBottomSheet;


public class UploadFragment extends Fragment {

    private static final String TAG = "UploadFragment";
    private FragmentUploadBinding binding;

    public UploadFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Upload");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUploadBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //TODO: Bottom sheet needs to come above bottom nav bar
        ModalBottomSheet modalBottomSheet = new ModalBottomSheet();
        modalBottomSheet.show(getActivity().getSupportFragmentManager(), ModalBottomSheet.TAG);
    }
}
