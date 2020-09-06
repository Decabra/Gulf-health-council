package com.example.ghc;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
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

import java.io.IOException;
import java.util.Objects;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private ProgressDialog progressDialog;
    private Intent intent;
    private EditText loginEditText;
    private FetchData fetchData = new FetchData();
    private String passwordInput = "";
    private NetworkConsistency networkConsistency;
    private AlertDialog alertDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        fetchData.setupUI(findViewById(R.id.loginSurface), LoginActivity.this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN); //it is to initially hide keyboard on login screen

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        networkConsistency = new NetworkConsistency(this);
        loginEditText = findViewById(R.id.loginEditText);
        Button loginPasswordButton = findViewById(R.id.loginPasswordButton);
        loginPasswordButton.setOnClickListener(this);
        alertDialog = fetchData.AlertDialogMessage(alertDialogBuilder);
    }
    //        alertDialog.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                if (!passwordInput.equals("") &&  networkConsistency.NetworkStatus()) {
//                    Log.d("Internet: ", "Connected");
//                    LoginActivity.AsyncTaskRunner runner = new LoginActivity.AsyncTaskRunner();
//                    runner.execute();
//                }
//                else{
//                    alertDialog.show();
//                }
//            }
//        });


//    @Override
//    protected void onDestroy() {
//        progressDialog.dismiss();
//        super.onDestroy();
//    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.loginPasswordButton:
                passwordInput = loginEditText.getText().toString();
                if (networkConsistency.NetworkStatus()) {
                    if (!passwordInput.equals("")) {
                        Log.d("Internet: ", "Connected");
                        AsyncTaskRunner runner = new AsyncTaskRunner();
                        runner.execute();
                    }
                    else {
                        loginEditText.setError("Required field");
                    }
                }
                else{
                    if (!alertDialog.isShowing()){
                        alertDialog.show();
                    }
                    Log.d("Internet: ", "Not Connected");
                }

                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private boolean applicationGateway() {
        Response response;
        String responseBody = null;
        boolean decision = false;
        OkHttpClient client = new OkHttpClient();
        String verifyURL = "https://work.appizia.com/lb/api/verify-password";
        RequestBody requestBody = new FormBody.Builder().add("password", passwordInput).build();
        Request request = new Request.Builder().url(verifyURL).post(requestBody).build();
        try {
            response = client.newCall(request).execute();
            responseBody = Objects.requireNonNull(response.body()).string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert responseBody != null;
        decision = !responseBody.equals("\"Invalid Password!\"");
        return decision;
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(@NotNull Call call, @NotNull IOException e) {
//
//            }
//            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
//            @Override
//            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
//                String responseBody = response.body().string();
//                Log.d("Response", responseBody);
//                if (!responseBody.equals("\"Invalid Password!\"")){
//                    decision = true;
//                }
//                else {
//                    decision = false;
////                    runOnUiThread(new Runnable() {
////                        @Override
////                        public void run() {
////                        }
////                    });
//                }
//            }
//        });
    }

    private class AsyncTaskRunner extends AsyncTask{
        long startTime;
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPreExecute() {
            startTime = System.currentTimeMillis();
            progressDialog = new ProgressDialog(LoginActivity.this);
            intent = new Intent(LoginActivity.this, MainActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            fetchData.progressLoader(progressDialog);
            super.onPreExecute();
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected Object doInBackground(Object[] objects) {
            return applicationGateway();
        }

        @Override
        protected void onPostExecute(Object o) {
            Log.d("Print decision",""+o);
            if (o.equals(true)){
                startActivity(intent);
            }
            else{
                loginEditText.setError("Wrong password");
            }
            progressDialog.dismiss();
            long elapsedTime = System.currentTimeMillis() - startTime;
            long elapsedSeconds = elapsedTime / 1000;
            Log.d("Time elapsed",""+elapsedSeconds);
            super.onPostExecute(o);
        }
    }
}

