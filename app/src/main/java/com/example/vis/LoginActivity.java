package com.example.vis;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.widget.Button;
import android.widget.EditText;


import com.example.vis.databinding.ActivityLoginBinding;



public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private EditText username;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.register.setOnClickListener(register ->{
            Intent intent = new Intent(this, RegistrationActivity.class);
            startActivity(intent);
        });
        binding.login.setOnClickListener(login ->{
            username = findViewById(R.id.nameField);
            password = findViewById(R.id.passField);
        if (username.getText().toString().equals("peter") && password.getText().toString().equals("peter")){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);

        }
        else if (username.getText().toString().equals("i") && password.getText().toString().equals("i")){
            Intent intent = new Intent(this, TeacherMainActivity.class);
            startActivity(intent);

        }
        else if (username.getText().toString().equals("") || password.getText().toString().equals("")){
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle(Html.fromHtml("<font color='#FF0000'>"+"Nesprávne prihlásenie !"+"</font>"))
                    .setMessage(Html.fromHtml("<font color='#FFFFFF'>"+"Musíte vyplniť všetky polia !"+"</font>"))
                    .setNeutralButton(Html.fromHtml("<font color='#FFFFFF'>"+"Skúste to znova."+"</font>"),(dialogInterface, i) -> dialogInterface.dismiss())
                    .create();
            dialog.show();
        }

        else{
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle(Html.fromHtml("<font color='#FF0000'>"+"Nesprávne prihlásenie !"+"</font>"))
                    .setMessage(Html.fromHtml("<font color='#FFFFFF'>"+"Zadali ste nesprávne meno alebo heslo !"+"</font>"))
                    .setNeutralButton(Html.fromHtml("<font color='#FFFFFF'>"+"Skúste to znova."+"</font>"),(dialogInterface, i) -> dialogInterface.dismiss())
                    .create();
            dialog.show();
        }
        });
    }
}