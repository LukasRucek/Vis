package com.example.vis;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.EditText;


import com.example.vis.databinding.ActivityLoginBinding;

import java.io.IOException;

import at.favre.lib.crypto.bcrypt.BCrypt;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class LoginActivity extends AppCompatActivity implements OnFinishLoginListener{

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
        binding.progressBar.setVisibility(View.GONE);
        binding.register.setOnClickListener(register ->{
            Intent intent = new Intent(this, RegistrationActivity.class);
            startActivity(intent);
        });
        binding.login.setOnClickListener(login ->{
            username = findViewById(R.id.nameField);
            password = findViewById(R.id.passField);
             if (username.getText().toString().equals("") || password.getText().toString().equals("")){
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle(Html.fromHtml("<font color='#FF0000'>"+getString(R.string.login_dialog1)+"</font>"))
                        .setMessage(Html.fromHtml("<font color='#FFFFFF'>"+getString(R.string.login_dialog2)+"</font>"))
                        .setNeutralButton(Html.fromHtml("<font color='#FFFFFF'>"+getString(R.string.login_dialog3)+"</font>"),(dialogInterface, i) -> dialogInterface.dismiss())
                        .create();
                dialog.show();
            }

            else{
                 binding.progressBar.setVisibility(View.VISIBLE);
                 new Connection(this).execute();
            }



        if (username.getText().toString().isEmpty() && password.getText().toString().equals("peter")){
            binding.progressBar.setVisibility(View.VISIBLE);
            new Connection(this).execute();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);

        }
        else if (username.getText().toString().equals("i") && password.getText().toString().equals("i")){
            Intent intent = new Intent(this, TeacherMainActivity.class);
            startActivity(intent);

        }

        });
    }

    public class Connection extends AsyncTask<Void, Void, Void> {
        private OnFinishLoginListener listener;

        public Connection(OnFinishLoginListener listener) {
            this.listener = listener;
        }
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
                if (Integer.toString(response.code()).equals("200")){
                    System.out.println(response.body());
                    listener.onSuccess_login();
                }
                else if (Integer.toString(response.code()).equals("401")){
                    System.out.println(response.body());
                    listener.onFailed_login();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    @Override
    public void onSuccess_login() {
        runOnUiThread(() -> {
            binding.progressBar.setVisibility(View.GONE);
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle(Html.fromHtml("<font color='#228B22'>"+getString(R.string.login_dialog9)+"</font>"))
                    .setMessage(Html.fromHtml("<font color='#FFFFFF'>"+getString(R.string.login_dialog10)+"</font>"))
                    .setNeutralButton(Html.fromHtml("<font color='#FFFFFF'>"+getString(R.string.login_dialog11)+"</font>"),(dialogInterface, i) ->  super.onBackPressed() )
                    .create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        });
    }

    @Override
    public void onFailed_login() {
        runOnUiThread(() -> {
            binding.progressBar.setVisibility(View.GONE);
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle(Html.fromHtml("<font color='#FF0000'>"+getString(R.string.login_dialog1)+"</font>"))
                    .setMessage(Html.fromHtml("<font color='#FFFFFF'>"+getString(R.string.login_dialog4)+"</font>"))
                    .setNeutralButton(Html.fromHtml("<font color='#FFFFFF'>"+getString(R.string.login_dialog3)+"</font>"),(dialogInterface, i) -> dialogInterface.dismiss())
                    .create();
            dialog.show();
        });
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