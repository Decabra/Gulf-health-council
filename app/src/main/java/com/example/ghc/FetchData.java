package com.example.ghc;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.view.inputmethod.InputMethodManager;
import android.widget.Spinner;

import androidx.annotation.RequiresApi;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FetchData{

    private JSONObject jsonData;
    private JSONObject options;
    private JSONObject your_country;
    private JSONObject country_to_travel;
    private JSONObject your_city;
    private JSONObject alert_medical_center;
    private JSONObject send_medical_list_after_minutes;
    private JSONArray check_after_time;




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
        String reponseBody = response.body().string();
        try {
            JSONObject json = new JSONObject(reponseBody);
            options = json.getJSONObject("options");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return options;
    }

    public void setOptions(JSONObject options) {
        this.options = options;
    }

    public JSONObject getYour_country() {
        return your_country;
    }

    public void setYour_country(JSONObject your_country) {
        this.your_country = your_country;
    }

    public JSONObject getCountry_to_travel() {
        return country_to_travel;
    }

    public void setCountry_to_travel(JSONObject country_to_travel) {
        this.country_to_travel = country_to_travel;
    }

    public JSONObject getYour_city() {
        return your_city;
    }

    public void setYour_city(JSONObject your_city) {
        this.your_city = your_city;
    }

    public JSONObject getAlert_medical_center() {
        return alert_medical_center;
    }

    public void setAlert_medical_center(JSONObject alert_medical_center) {
        this.alert_medical_center = alert_medical_center;
    }

    public JSONObject getSend_medical_list_after_minutes() {
        return send_medical_list_after_minutes;
    }

    public void setSend_medical_list_after_minutes(JSONObject send_medical_list_after_minutes) {
        this.send_medical_list_after_minutes = send_medical_list_after_minutes;
    }

    public JSONArray getCheck_after_time() {
        return check_after_time;
    }

    public void setCheck_after_time(JSONArray check_after_time) {
        this.check_after_time = check_after_time;
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

    void setSpinnerHeight(Spinner spinner, int height){
        try {
            Field popup = Spinner.class.getDeclaredField("mPopup");
            popup.setAccessible(true);

            // Get private mPopup member variable and try cast to ListPopupWindow
            android.widget.ListPopupWindow popupWindow = (android.widget.ListPopupWindow) popup.get(spinner);

            // Set popupWindow height to 500px
            popupWindow.setHeight(height);
        }
        catch (NoClassDefFoundError | ClassCastException | NoSuchFieldException | IllegalAccessException e) {
            // silently fail...
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    void progressLoader(ProgressDialog ProgressLoader){
        ProgressLoader.show();
        ProgressLoader.setContentView(R.layout.progress_dialog);
        Objects.requireNonNull(ProgressLoader.getWindow()).setBackgroundDrawableResource(R.color.transparent);
        ProgressLoader.setCanceledOnTouchOutside(false);
    }
}
