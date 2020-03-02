package com.example.ghc;

import org.json.JSONArray;
import org.json.JSONObject;

public class FetchData {

    private JSONObject jsonData;
    private JSONObject options;
    private JSONObject your_country;
    private JSONObject country_to_travel;
    private JSONObject your_city;
    private JSONObject alert_medical_center;
    private JSONObject send_medical_list_after_minutes;
    private JSONArray check_after_time;

    public void getRequestMethod(){

    }


    public JSONObject getJsonData() {
        return jsonData;
    }

    public void setJsonData(JSONObject jsonData) {
        this.jsonData = jsonData;
    }

    public JSONObject getOptions() {
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







}
