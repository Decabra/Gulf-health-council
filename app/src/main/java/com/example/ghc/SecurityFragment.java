package com.example.ghc;

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
    private String verifyURL = "https://work.appizia.com/lb/api/verify-password";
    private String settingUpdateURL = "https://work.appizia.com/lb/api/update-settings";

    private int verifyPasswordCounter;
    private OkHttpClient client;
    private FetchData fetchData;
    private String GiantOldPassword;
    private String GiantNewPassword;
    private String GiantConfirmPassword;
    private NetworkConsistency networkConsistency;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.security_section_layout, container, false);

        fetchData = new FetchData(getContext());
        networkConsistency = new NetworkConsistency(getContext());

        fetchData.setupUI(rootView.findViewById(R.id.securitySurface), getActivity());
//        fetchData.cookProgressDialog();
        fetchData.alertDialog = fetchData.AlertDialogMessage(networkConsistency.internetDisconnectedMessage);

        oldPasswordTextField = rootView.findViewById(R.id.oldPasswordTextField);
        newPasswordTextField = rootView.findViewById(R.id.newPasswordTextField);
        confirmPasswordTextField = rootView.findViewById(R.id.confirmPasswordTextField);
        Button securityUpdateButton = rootView.findViewById(R.id.securityUpdateButton);

        securityUpdateButton.setOnClickListener(this);

        client = new OkHttpClient();

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
//                fetchData.progressDialog.dismiss();
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
//            fetchData.progressDialog.show();
            passwordVerificationPostRequest(verifyURL);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.securityUpdateButton) {
            if(networkConsistency.networkStatus()) {
                preCheckups();
            }
            else{
                if (!fetchData.alertDialog.isShowing()){
                    fetchData.alertDialog.show();
                }
            }
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
