package com.example.ghc;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

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

        loginEditText = findViewById(R.id.loginEditText);

        fetchData.setupUI(findViewById(R.id.loginSurface), LoginActivity.this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN); //it is to initially hide keyboard on login screen

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        networkConsistency = new NetworkConsistency(this);

        Button loginPasswordButton = findViewById(R.id.loginPasswordButton);
        loginPasswordButton.setOnClickListener(this);

        alertDialog = fetchData.AlertDialogMessage(alertDialogBuilder, "Internet disconnected!");

        NetworkAsyncTaskRunner networkAsyncTaskRunner = new NetworkAsyncTaskRunner();
        networkAsyncTaskRunner.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        Log.d("periodicChecker: ", ""+ networkConsistency.periodicNetworkStateChecker);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        networkConsistency.stopRepeatingTask();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginPasswordButton:
                passwordInput = loginEditText.getText().toString();
                if (networkConsistency.networkStatus()) {
                    if (!passwordInput.equals("")) {
                        Log.d("Internet: ", "Connected");
                        GatewayAsyncTaskRunner gatewayAsyncTaskRunner = new GatewayAsyncTaskRunner();
                        gatewayAsyncTaskRunner.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    } else {
                        loginEditText.setError("Required field");
                    }
                } else {
                    if (!alertDialog.isShowing()) {
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
        boolean decision;
        //Have to work on Timeouts
//        OkHttpClient client = new OkHttpClient().newBuilder().callTimeout(1 , TimeUnit.SECONDS).build();
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
        Log.d("responseBody: ", "" + responseBody);
        assert responseBody != null;
        decision = !responseBody.equals("\"Invalid Password!\"");
        return decision;
    }

    protected void networkConsistencyOutcomes(boolean checker){
        if (checker){
//            Progress
        }

    }

    @SuppressLint("StaticFieldLeak")
    private class GatewayAsyncTaskRunner extends AsyncTask<Boolean, Boolean, Boolean> {
        //        long startTime;
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPreExecute() {
//            startTime = System.currentTimeMillis();
            progressDialog = new ProgressDialog(LoginActivity.this);
            intent = new Intent(LoginActivity.this, MainActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            fetchData.progressLoader(progressDialog);
            super.onPreExecute();
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected Boolean doInBackground(Boolean... booleans) {
            return applicationGateway();
        }

        @Override
        protected void onPostExecute(Boolean o) {
//            Log.d("Print decision", "" + o);
            if (o) {
                startActivity(intent);
            } else {
//                Log.d("Print decision", "" + o);
                loginEditText.setError("Wrong password");
            }
            progressDialog.dismiss();
//            long elapsedTime = System.currentTimeMillis() - startTime;
//            long elapsedSeconds = elapsedTime / 1000;
//            Log.d("Time elapsed",""+elapsedSeconds);
            super.onPostExecute(o);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class NetworkAsyncTaskRunner extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            networkConsistency.startRepeatingTask();
            return null;
        }
    }
}
