package com.example.vis;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.EditText;


import com.example.vis.databinding.ActivityLoginBinding;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private EditText username;
    private EditText password;
    private String lang;
    private LanguageManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manager = new LanguageManager(this);
        manager.updateResource();
        lang = manager.getLanguage();
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.register.setOnClickListener(register ->{
            Intent intent = new Intent(this, RegistrationActivity.class);
            startActivity(intent);
        });
        binding.login.setOnClickListener(login ->{
            username = findViewById(R.id.nameField);
            password = findViewById(R.id.passField);
            new Connection().execute();
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
                    .setTitle(Html.fromHtml("<font color='#FF0000'>"+getString(R.string.login_dialog1)+"</font>"))
                    .setMessage(Html.fromHtml("<font color='#FFFFFF'>"+getString(R.string.login_dialog2)+"</font>"))
                    .setNeutralButton(Html.fromHtml("<font color='#FFFFFF'>"+getString(R.string.login_dialog3)+"</font>"),(dialogInterface, i) -> dialogInterface.dismiss())
                    .create();
            dialog.show();
        }

        else{
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle(Html.fromHtml("<font color='#FF0000'>"+getString(R.string.login_dialog1)+"</font>"))
                    .setMessage(Html.fromHtml("<font color='#FFFFFF'>"+getString(R.string.login_dialog4)+"</font>"))
                    .setNeutralButton(Html.fromHtml("<font color='#FFFFFF'>"+getString(R.string.login_dialog3)+"</font>"),(dialogInterface, i) -> dialogInterface.dismiss())
                    .create();
            dialog.show();
        }
        });
    }

    public class Connection extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... arg0) {
            OkHttpClient client = new OkHttpClient();
            String username = binding.nameField.getText().toString();
            String password = binding.passField.getText().toString();
            Request request = new Request.Builder()
                    .url("http://192.168.137.1:8000/vis/login/"+username+"/"+password)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                Log.d("HTTPCALL", Integer.toString(response.code()));
                System.out.println(response.body().string());

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    @Override
    protected void onResume() {
        if (!lang.equals(manager.getLanguage())) {
            finish();
            startActivity(getIntent());
        }
        super.onResume();
    }
}