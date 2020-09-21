package com.example.ghc;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FetchData{

    private JSONObject jsonData;
    private Context context;
    private JSONObject options;
    private JSONObject your_country;
    private JSONObject country_to_travel;
    private JSONObject your_city;
    private JSONObject alert_medical_center;
    private JSONObject send_medical_list_after_minutes;
    private JSONArray check_after_time;
    protected AlertDialog alertDialog;
    protected AlertDialog.Builder alertDialogBuilder;
    protected ProgressDialog progressDialog;


    FetchData(Context context){
        this.context = context;
    }

    public JSONObject getJsonData() {
        return jsonData;
    }

    public void setJsonData(JSONObject jsonData) {
        this.jsonData = jsonData;
    }

    JSONObject getOptions(String getOptionURL, OkHttpClient client) throws IOException, JSONException {
        JSONObject options = null;
        Request request = new Request.Builder().url(getOptionURL).get().build();
        Response response = client.newCall(request).execute();
        String responseBody = response.body().string();
        try {
            JSONObject json = new JSONObject(responseBody);
            options = json.getJSONObject("options");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return options;
    }

    JSONObject getSettings(String getSettingsURL, OkHttpClient client) throws IOException, JSONException {
        JSONObject settings = null;
        Request request = new Request.Builder().url(getSettingsURL).get().build();
        Response response = client.newCall(request).execute();
        String responseBody = response.body().string();
        JSONObject json = new JSONObject(responseBody);
        settings = json.getJSONObject("settings");
        return settings;
    }
//
//    public void setOptions(JSONObject options) {
//        this.options = options;
//    }
//
//    public JSONObject getYour_country() {
//        return your_country;
//    }
//
//    public void setYour_country(JSONObject your_country) {
//        this.your_country = your_country;
//    }
//
//    public JSONObject getCountry_to_travel() {
//        return country_to_travel;
//    }
//
//    public void setCountry_to_travel(JSONObject country_to_travel) {
//        this.country_to_travel = country_to_travel;
//    }
//
//    public JSONObject getYour_city() {
//        return your_city;
//    }
//
//    public void setYour_city(JSONObject your_city) {
//        this.your_city = your_city;
//    }
//
//    public JSONObject getAlert_medical_center() {
//        return alert_medical_center;
//    }
//
//    public void setAlert_medical_center(JSONObject alert_medical_center) {
//        this.alert_medical_center = alert_medical_center;
//    }
//
//    public JSONObject getSend_medical_list_after_minutes() {
//        return send_medical_list_after_minutes;
//    }
//
//    public void setSend_medical_list_after_minutes(JSONObject send_medical_list_after_minutes) {
//        this.send_medical_list_after_minutes = send_medical_list_after_minutes;
//    }
//
//    public JSONArray getCheck_after_time() {
//        return check_after_time;
//    }
//
//    public void setCheck_after_time(JSONArray check_after_time) {
//        this.check_after_time = check_after_time;
//    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    void cookProgressDialog(){
        progressDialog = new ProgressDialog(context);
        progressDialog.setContentView(R.layout.progress_dialog);
        Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawableResource(R.color.transparent);
        progressDialog.setCanceledOnTouchOutside(false);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null){
            inputMethodManager.hideSoftInputFromWindow(activity.getWindow().getDecorView().getRootView().getWindowToken(), 0);
        }
    }

    void setupUI(View view, final Activity activity) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(Objects.requireNonNull(activity));
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView, activity);
            }
        }
    }

    AlertDialog AlertDialogMessage(String message){
        alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setCancelable(true);
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        return alertDialogBuilder.create();
    }


}
