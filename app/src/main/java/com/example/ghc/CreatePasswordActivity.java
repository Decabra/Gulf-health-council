package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CreatePasswordActivity extends AppCompatActivity implements View.OnClickListener {
    EditText oldPassword, editText2, editText1;

    Button button;
    /*
//GET Request
    private TextView oldPass;


    @Override
    public <T extends View> T findViewById(int id) {
        oldPass =  super.findViewById(R.id.oldPassword);
        return null;
    }

OkHttpClient client = new OkHttpClient();

    String url = "https://cabinal.com/lb/api/verify-password";

    Request request = new Request.Builder()
            .url(url)
            .build();
//client.newCall()
*/

    public void oldPassPostRequest() throws IOException {
        String oldPass = oldPassword.getText().toString().trim();
        String URL = "https://cabinal.com/lb/api/verify-password";
        OkHttpClient client = new OkHttpClient();
        RequestBody reqBody = new FormBody.Builder().add("password", oldPass).build();
        Request request = new Request.Builder().url(URL).post(reqBody).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                String responseBody = response.body().string();
                Log.d("My tag: ", responseBody);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_password);

        oldPassword = (EditText) findViewById(R.id.oldPasssword);
        editText1 = (EditText) findViewById(R.id.editText1);
        editText2 = (EditText) findViewById(R.id.editText2);
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(this);

    }
//
    // I am using the given method for checking special Character. you can replace it's regex according to your need.

    public Boolean isSpecialCharAvailable(String s) {
        //int counter =0;
        if (s == null || s.trim().isEmpty()) {
            return false;
        }
        Pattern p = Pattern.compile("[^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\\\S+$).{4,}$]");//special character reqquired
        Matcher m = p.matcher(s);
        // boolean b = m.matches();

        boolean b = m.find();
        if (b == true)
            return true;
        else
            return false;
    }

    @Override
    public void onClick(View view) {
        try {
            oldPassPostRequest();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String text1 = editText1.getText().toString();
        String text2 = editText2.getText().toString();
        if (text1.equals("") || text2.equals("")) {
            //No Password Present
            Toast.makeText(CreatePasswordActivity.this, "No Password Entered", Toast.LENGTH_SHORT).show();
            //Toas.makeText(CreatePasswordActivity.this, "No Password Entered", Toast.LENGTH_SHORT).show();
        }
        if (text1 != text2) {
            // Passwords don't match
            Toast.makeText(CreatePasswordActivity.this, "Passwords Don't Match", Toast.LENGTH_SHORT).show();
            //Toas.makeText(CreatePasswordActivity.this, "No Password Entered", Toast.LENGTH_SHORT).show();
        } else {
            if (text1.equals(text2)) {
                //Check that conditions are met
                if (text1.length() < 8) {
                    // Password Length Too Short
                    Toast.makeText(CreatePasswordActivity.this, "PasswordLength is too Short", Toast.LENGTH_SHORT).show();
                }
                if (isSpecialCharAvailable(text1) == false) {
                    // Password doesn't contain valid characters
                    Toast.makeText(CreatePasswordActivity.this, "Password must contain at least one" +
                            "captial letter, one lower case letter, and one special character.", Toast.LENGTH_SHORT).show();
                }
                //Save the password
                SharedPreferences settings = getSharedPreferences("PREFS", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("Password", text1);
                editor.apply();

                //Enter the App
                //You will enter mainactivity here wen integrating woth master application
                // Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                // startActivity(intent);
                finish();
            } else {
                Toast.makeText(CreatePasswordActivity.this, "Passwords Don't Match", Toast.LENGTH_LONG).show();
            }
        }
    }
}

