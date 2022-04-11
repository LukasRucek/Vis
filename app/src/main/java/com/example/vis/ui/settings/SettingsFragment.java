package com.example.vis.ui.settings;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;


import com.example.vis.LanguageManager;
import com.example.vis.LocaleHelper;
import com.example.vis.LoginActivity;
import com.example.vis.MainActivity;
import com.example.vis.R;
import com.example.vis.RegistrationActivity;

import com.example.vis.databinding.FragmentSettingsBinding;
import com.example.vis.ui.message.MessageFragment;

import java.util.Locale;


public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private EditText oldpassword;
    private EditText newpassword;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        LanguageManager lang = new LanguageManager(getActivity());
        binding.buttonSVK.setOnClickListener(View -> {
            lang.updateResource("sk");
            getActivity().recreate();

        });
        binding.buttonENG.setOnClickListener(View -> {
            lang.updateResource("en");
            getActivity().recreate();
        });

        binding.buttonChangePassword.setOnClickListener(View ->{

        });

        binding.logOut.setOnClickListener(View -> {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        });

        return root;
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}