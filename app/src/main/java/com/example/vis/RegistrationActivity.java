package com.example.vis;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Patterns;
import android.widget.EditText;


import com.example.vis.databinding.ActivityRegistrationBinding;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLEncoder;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

public class RegistrationActivity extends AppCompatActivity {
    private ActivityRegistrationBinding binding;
    public final Pattern textPattern = Pattern.compile("^(?=.*[A-Z]).+$");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.registration.setOnClickListener(register ->{
            if (checkDataEntered()){
                new Connection().execute();
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle(Html.fromHtml("<font color='#228B22'>"+"Úspešná registrácia !"+"</font>"))
                        .setMessage(Html.fromHtml("<font color='#FFFFFF'>"+"Úspešne sa vám podarilo vytvoriť účet !"+"</font>"))
                        .setNeutralButton(Html.fromHtml("<font color='#FFFFFF'>"+"Kliknite tu!"+"</font>"),(dialogInterface, i) ->  super.onBackPressed() )
                        .create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }
        });
    }
    public class Connection extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... arg0){
            try {
                HttpsURLConnection connection = (HttpsURLConnection) new URL("https://192.168.42.72:8000/vis/register").openConnection();
                connection.setRequestMethod("POST");
                String postData = "user_name:" + URLEncoder.encode(binding.username.getText().toString());
                postData += "&first_name:" + URLEncoder.encode(binding.firstname.getText().toString());
                postData += "&last_name" + URLEncoder.encode(binding.lastname.getText().toString());
                postData += "&type:" + URLEncoder.encode("student");
                postData += "&password:" + URLEncoder.encode(binding.password.getText().toString());

                connection.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
                wr.write(postData);
                wr.flush();

                int responseCode = connection.getResponseCode();
                if(responseCode == 200){
                    System.out.println("POST was successful.");
                }
                else {
                    System.out.println("Wrong password.");
                }
            } catch (IOException e) {
                System.out.println("Ahoj.");
                e.printStackTrace();
            }
            return null;
        }
    }
    boolean isEmpty(EditText field) {
        if (field.getText().toString().equals("")){
            return true;
        }
        return false;

    }

    boolean checkDataEntered() {

        if (isEmpty(binding.firstname)) {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle(Html.fromHtml("<font color='#FF0000'>"+"Nesprávna registrácia !"+"</font>"))
                    .setMessage(Html.fromHtml("<font color='#FFFFFF'>"+"Musíte vyplniť meno !"+"</font>"))
                    .setNeutralButton(Html.fromHtml("<font color='#FFFFFF'>"+"Skúste to znova."+"</font>"),(dialogInterface, i) -> dialogInterface.dismiss())
                    .create();
            dialog.show();
            return false;
        }

        if (isEmpty(binding.lastname)) {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle(Html.fromHtml("<font color='#FF0000'>"+"Nesprávna registrácia !"+"</font>"))
                    .setMessage(Html.fromHtml("<font color='#FFFFFF'>"+"Musíte vyplniť priezvisko !"+"</font>"))
                    .setNeutralButton(Html.fromHtml("<font color='#FFFFFF'>"+"Skúste to znova."+"</font>"),(dialogInterface, i) -> dialogInterface.dismiss())
                    .create();
            dialog.show();
            return false;
        }

        if (isEmpty(binding.username)) {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle(Html.fromHtml("<font color='#FF0000'>"+"Nesprávna registrácia !"+"</font>"))
                    .setMessage(Html.fromHtml("<font color='#FFFFFF'>"+"Musíte vyplniť Vis Meno !"+"</font>"))
                    .setNeutralButton(Html.fromHtml("<font color='#FFFFFF'>"+"Skúste to znova."+"</font>"),(dialogInterface, i) -> dialogInterface.dismiss())
                    .create();
            dialog.show();
            return false;
        }

        if (isEmpty(binding.password) || binding.password.getText().toString().length() < 8 || !textPattern.matcher(binding.password.getText().toString()).matches() || !binding.password.getText().toString().matches(".*[0-9].*")) {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle(Html.fromHtml("<font color='#FF0000'>"+"Nesprávna registrácia !"+"</font>"))
                    .setMessage(Html.fromHtml("<font color='#FFFFFF'>"+"Musíte vyplniť správny formát hesla !"+"</font>"))
                    .setMessage(Html.fromHtml("<font color='#FFFFFF'>"+"Heslo musí obsahovať čislo, veľké písmeno a aspoň 8 znakov !"+"</font>"))
                    .setNeutralButton(Html.fromHtml("<font color='#FFFFFF'>"+"Skúste to znova."+"</font>"),(dialogInterface, i) -> dialogInterface.dismiss())
                    .create();
            dialog.show();
            return false;
        }

        if (isEmpty(binding.email) || !Patterns.EMAIL_ADDRESS.matcher(binding.email.getText().toString()).matches()) {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle(Html.fromHtml("<font color='#FF0000'>"+"Nesprávna registrácia !"+"</font>"))
                    .setMessage(Html.fromHtml("<font color='#FFFFFF'>"+"Nesprávny format emailu !"+"</font>"))
                    .setNeutralButton(Html.fromHtml("<font color='#FFFFFF'>"+"Skúste to znova."+"</font>"),(dialogInterface, i) -> dialogInterface.dismiss())
                    .create();
            dialog.show();
            return false;
        }

        if (isEmpty(binding.schoolId)) {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle(Html.fromHtml("<font color='#FF0000'>"+"Nesprávna registrácia !"+"</font>"))
                    .setMessage(Html.fromHtml("<font color='#FFFFFF'>"+"Musíte vyplniť id školy !"+"</font>"))
                    .setNeutralButton(Html.fromHtml("<font color='#FFFFFF'>"+"Skúste to znova."+"</font>"),(dialogInterface, i) -> dialogInterface.dismiss())
                    .create();
            dialog.show();
            return false;
        }

        if (isEmpty(binding.phone) || binding.phone.getText().length() != 10) {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle(Html.fromHtml("<font color='#FF0000'>"+"Nesprávna registrácia !"+"</font>"))
                    .setMessage(Html.fromHtml("<font color='#FFFFFF'>"+"Telefóne číslo musí mať 10 číslic !"+"</font>"))
                    .setNeutralButton(Html.fromHtml("<font color='#FFFFFF'>"+"Skúste to znova."+"</font>"),(dialogInterface, i) -> dialogInterface.dismiss())
                    .create();
            dialog.show();
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(Html.fromHtml("<font color='#FF0000'>"+"Prerušenie registrácie !"+"</font>"))
                .setMessage("Vážne chcete prerušiť registráciu ?")
                .setPositiveButton(Html.fromHtml("<font color='#228B22'>"+"Áno"+"</font>"),(dialogInterface, i) ->  super.onBackPressed())
                .setNegativeButton(Html.fromHtml("<font color='#228B22'>"+"Nie"+"</font>"),(dialogInterface, i) -> dialogInterface.dismiss())
                .create();
        dialog.show();
    }
}
