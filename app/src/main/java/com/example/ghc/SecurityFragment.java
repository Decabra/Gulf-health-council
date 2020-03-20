package com.example.ghc;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.util.Objects;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SecurityFragment extends Fragment implements View.OnClickListener {

    private EditText oldPasswordTextField;
    private EditText newPasswordTextField;
    private EditText confirmPasswordTextField;
    private String verifyURL;
    private String settingUpdateURL;
    private int verifyPasswordCounter;
    private OkHttpClient client;
    private ProgressDialog progressDialog;
    private FetchData fetchData;
    private String GiantOldPassword;
    private String GiantNewPassword;
    private String GiantConfirmPassword;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.security_section_layout, container, false);

        oldPasswordTextField = rootView.findViewById(R.id.oldPasswordTextField);
        newPasswordTextField = rootView.findViewById(R.id.newPasswordTextField);
        confirmPasswordTextField = rootView.findViewById(R.id.confirmPasswordTextField);
        Button securityUpdateButton = rootView.findViewById(R.id.securityUpdateButton);
        verifyURL = "https://cabinal.com/lb/api/verify-password";
        settingUpdateURL = "https://cabinal.com/lb/api/update-settings";

        securityUpdateButton.setOnClickListener(this);

        fetchData = new FetchData();
        client = new OkHttpClient();

        //loader
        progressDialog = new ProgressDialog(getActivity());

        return rootView;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void passwordVerificationPostRequest(String URL) {
        verifyPasswordCounter = 0;
        RequestBody requestBody = new FormBody.Builder().add("password", GiantOldPassword).build();
        Request request = new Request.Builder().url(URL).post(requestBody).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String verifyPasswordResponse = Objects.requireNonNull(response.body()).string();
                if (verifyPasswordResponse.equals("\"Invalid Password!\"")){
                    verifyPasswordCounter = 1;
                    Log.d("verifyPasswordCounter",""+verifyPasswordCounter);
                    Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            oldPasswordTextField.setError("Wrong password");
                        }
                    });
                }
                else{
                    updatePasswordPostRequest(settingUpdateURL);
                }
                progressDialog.dismiss();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void updatePasswordPostRequest(String URL) throws IOException {
        RequestBody requestBody = new FormBody.Builder()
                .add("old_password", GiantOldPassword)
                .add("new_password", GiantNewPassword)
                .add("confirm_new_password", GiantConfirmPassword).build();
        Request request = new Request.Builder().url(URL).post(requestBody).build();
        Response response = client.newCall(request).execute();
        Log.d("Response", Objects.requireNonNull(response.body()).string());
        toastMessage();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void preCheckups() {
        GiantOldPassword = oldPasswordTextField.getText().toString().trim();
        GiantNewPassword = newPasswordTextField.getText().toString().trim();
        GiantConfirmPassword = confirmPasswordTextField.getText().toString().trim();
        if ( GiantOldPassword.equals("")){
            oldPasswordTextField.setError("Wrong password");
        }
        else if (GiantNewPassword.equals("")){
            newPasswordTextField.setError("Enter a password");
        }
        else if ( GiantNewPassword.length() < 8){
            newPasswordTextField.setError("Use 8 characters or more for your password");
        }
        else if (GiantConfirmPassword.equals("")){
            confirmPasswordTextField.setError("Confirm your password");
        }
        else if ( !GiantConfirmPassword.equals(GiantNewPassword) ){
            confirmPasswordTextField.setError("Passwords didn't match");
        }
        else {
            fetchData.progressLoader(progressDialog);
            passwordVerificationPostRequest(verifyURL);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.securityUpdateButton) {
            preCheckups();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void toastMessage(){
        Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), "Password updated", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
