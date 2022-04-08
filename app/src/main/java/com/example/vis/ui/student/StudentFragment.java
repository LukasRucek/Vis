package com.example.vis.ui.student;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.vis.databinding.FragmentStudentBinding;

public class StudentFragment extends Fragment {

    private FragmentStudentBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentStudentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.textGallery.setText("Ahoj Adam");

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}