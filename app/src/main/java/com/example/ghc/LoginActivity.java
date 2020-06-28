package com.example.ghc;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText loginEditText;
    private FetchData fetchData = new FetchData();

    private ProgressDialog progressDialog;
    private Intent intent;
    private String passwordInput = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressDialog = new ProgressDialog(this);
        intent = new Intent(this, MainActivity.class);
        loginEditText = findViewById(R.id.loginEditText);
        Button loginPasswordButton = findViewById(R.id.loginPasswordButton);
        loginPasswordButton.setOnClickListener(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN); //it is to initially hide keyboard on login screen


    }

    private void applicationGateway(){
        OkHttpClient client = new OkHttpClient();
        String verifyURL = "https://work.appizia.com/lb/api/verify-password";
        RequestBody requestBody = new FormBody.Builder().add("password", passwordInput).build();
        Request request = new Request.Builder().url(verifyURL).post(requestBody).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseBody = response.body().string();
                Log.d("Response", responseBody);
                if (!responseBody.equals("\"Invalid Password!\"")){
                    startActivity(intent);
                }
                else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loginEditText.setError("Wrong password");
                            progressDialog.dismiss();
                        }
                    });
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.loginPasswordButton:
                passwordInput = loginEditText.getText().toString();
                if (!passwordInput.equals("")) {
                    fetchData.progressLoader(progressDialog);
                    applicationGateway();
                }
                else{
                    loginEditText.setError("Wrong password");
                }
                break;
        }
    }
}
