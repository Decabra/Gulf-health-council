package com.example.ghc;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BasicFragment extends Fragment implements View.OnClickListener{

    private ArrayList<String> CountryName;
    private ArrayList<String> CityName;
    private ArrayList<String> TravelName;
    private ArrayList<String> MedicalCenterName;

    private HashMap<String, String> mapCountry;
    private HashMap<String, String> mapTravel;
    private HashMap<String, String> mapCity;
    private HashMap<String, String> mapMedicalCenter;

    private Spinner CountrySpinner;
    private Spinner CitySpinner;
    private Spinner TravelSpinner;
    private Spinner MedicalCenterSpinner;

     ProgressDialog ProgressLoader;

     TextView countryErrorLabel;
     TextView cityErrorLabel;
     TextView travelErrorLabel;
     TextView medicalErrorLabel;
    private TextView centerUpdateLabel; //medical center update message label


    private String GiantCountryKey;
    private String GiantCityKey;
    private String GiantTravelKey;
    private String GiantMedicalKey;

    String CountryToast;
     String CityToast;
     String TravelToast;
     String MedicalToast;

    private String getOptionURL;
    private String updateCenterURL;
    private String settingUpdateURL;

    private SpannableString spannableMessage;

    private int citySelectionCounter;
    private int countrySelectionCounter;
    private int travelSelectionCounter;
    private int medicalSelectionCounter;

    private Button updateButton;
    private ArrayAdapter<String> AlertAdapter;

    private OkHttpClient client;

    private FetchData fetchData;

    private JSONObject your_city;
    private  JSONObject json;
    private JSONObject options;
    private boolean postRequestDone;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View RootView = inflater.inflate(R.layout.basic_section_layout, container, false);

        client = new OkHttpClient();

        mapCountry = new HashMap<>();
        mapCity = new HashMap<>();
        mapTravel = new HashMap<>();
        mapMedicalCenter = new HashMap<>();

        CountryName = new ArrayList<>();
        CityName = new ArrayList<>();
        TravelName = new ArrayList<>();
        MedicalCenterName = new ArrayList<>();

        getOptionURL = "https://cabinal.com/lb/api/get-options-data";
        updateCenterURL = "https://cabinal.com/lb/api/update-medical-center";
        settingUpdateURL = "https://cabinal.com/lb/api/update-settings";

        citySelectionCounter = 0;
        countrySelectionCounter = 0;
        travelSelectionCounter = 0;
        medicalSelectionCounter = 0;



        CountrySpinner = RootView.findViewById(R.id.CountrySpinner);
        CitySpinner = RootView.findViewById(R.id.CitySpinner);
        TravelSpinner = RootView.findViewById(R.id.TravelSpinner);
        MedicalCenterSpinner = RootView.findViewById(R.id.MedicalCenterSpinner);

        //update button
        updateButton = RootView.findViewById(R.id.basicUpdateButton);
        updateButton.setOnClickListener(this);
        fetchData = new FetchData();
        //Spinner drop down menu height
        fetchData.setSpinnerHeight(MedicalCenterSpinner, 575);
        fetchData.setSpinnerHeight(CountrySpinner, 1330);
        fetchData.setSpinnerHeight(CitySpinner, 825);

        //loader
        ProgressLoader = new ProgressDialog(getActivity());
        //Error labels
        countryErrorLabel = RootView.findViewById(R.id.countryErrorLabel);
        countryErrorLabel.setVisibility(View.INVISIBLE);
        cityErrorLabel = RootView.findViewById(R.id.cityErrorLabel);
        cityErrorLabel.setVisibility(View.INVISIBLE);
        travelErrorLabel = RootView.findViewById(R.id.travelErrorLabel);
        travelErrorLabel.setVisibility(View.INVISIBLE);
        medicalErrorLabel = RootView.findViewById(R.id.medicalErrorLabel);
        medicalErrorLabel.setVisibility(View.INVISIBLE);
        //update centers message label
        centerUpdateLabel = RootView.findViewById(R.id.centerUpdateLabel);
        centerUpdateLabel.setVisibility(View.INVISIBLE);

        CountryName.add(0, "-Select your country-");
        TravelName.add(0, "-Select your GCC country-");

        loadCountrySpinner(getOptionURL);

        clickableText();


        CountrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                CountryToast = CountrySpinner.getItemAtPosition(CountrySpinner.getSelectedItemPosition()).toString();
                centerUpdateLabel.setVisibility(View.INVISIBLE);
                CityName.clear();
                mapCity.clear();
                CityName.add(0, "-Select your city-");
                if (!CountryToast.equals("-Select your country-")) {
                    countrySelectionCounter += 1;
                    countryErrorLabel.setVisibility(View.INVISIBLE); //error label
                    Toast.makeText(getActivity(), CountryToast, Toast.LENGTH_LONG).show();
                    for (String key : mapCountry.keySet()) {
                        if (mapCountry.get(key).equals(CountryToast)) {
                            try {
                                GiantCountryKey = key;
                                JSONArray array = your_city.getJSONArray(key);
                                for (int i = 0; i < array.length(); i++) {
                                    JSONArray subArray = array.getJSONArray(i);
                                    CityName.add(subArray.getString(1));
                                    mapCity.put(subArray.getString(0), subArray.getString(1));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } else if (CountryToast.equals("-Select your country-") && countrySelectionCounter > 0) {
                    countryErrorLabel.setVisibility(View.VISIBLE); //error label
                }
                Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CitySpinner.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, CityName));
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        CitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                postRequestDone = false;
                CityToast = CitySpinner.getItemAtPosition(CitySpinner.getSelectedItemPosition()).toString();
                if (CityToast.equals("-Select your city-") && citySelectionCounter > 0) {
                    cityErrorLabel.setVisibility(View.VISIBLE); //error label
                } else {
                    cityErrorLabel.setVisibility(View.INVISIBLE); //error label
                }
                loadMedicalCenterSpinner(CityToast);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        TravelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TravelToast = TravelSpinner.getItemAtPosition(TravelSpinner.getSelectedItemPosition()).toString();
                if (!TravelToast.equals("-Select your GCC country-")) {
                    travelSelectionCounter += 1;
                    travelErrorLabel.setVisibility(View.INVISIBLE);
                    for (String key : mapTravel.keySet()) {
                        if (mapTravel.get(key).equals(TravelToast)) {
                            GiantTravelKey = key;
                        }
                    }
                    Toast.makeText(getActivity(), TravelToast, Toast.LENGTH_SHORT).show();

                    Log.d("Giants: ", "GiantCountry: " + GiantCountryKey + " GiantTravel: " + GiantTravelKey + " GianCity: " + GiantCityKey);
                }
                else if (TravelToast.equals("-Select your GCC country-") && travelSelectionCounter > 0) {
                    travelErrorLabel.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        MedicalCenterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                MedicalToast = MedicalCenterSpinner.getItemAtPosition(MedicalCenterSpinner.getSelectedItemPosition()).toString();
                if (!MedicalToast.equals("-Select medical center-")) {
                    medicalErrorLabel.setVisibility(View.INVISIBLE);
                    Log.d("If", "Executed");
                    medicalSelectionCounter += 1;
                    for (String key : mapMedicalCenter.keySet()) {
                        if (mapMedicalCenter.get(key).equals(MedicalToast)) {
                            GiantMedicalKey = key;
                            Log.d("Medical Center Code", "" + GiantMedicalKey);
                        }
                    }
                } else if (MedicalToast.equals("-Select medical center-") && medicalSelectionCounter > 0) {
                    medicalErrorLabel.setTextColor(getResources().getColor(R.color.colorRed));
                    Log.d("Else if", "Executed");
                    medicalErrorLabel.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return RootView;
    }

    private void loadCountrySpinner(String URL){
        Request request = new Request.Builder().url(URL).get().build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseData = response.body().string();
                try {
                    json = new JSONObject(responseData);
                    options = json.getJSONObject("options");
                    JSONObject your_country = options.getJSONObject("your_country");
                    your_city = options.getJSONObject("your_city");
                    JSONObject country_to_travel = options.getJSONObject("country_to_travel");
                    Iterator travelIterator = country_to_travel.keys();
                    Iterator countryIterator = your_country.keys();
                    while (countryIterator.hasNext()){
                        String key = (String) countryIterator.next();
                        mapCountry.put(key, your_country.getString(key));
                        CountryName.add(mapCountry.get(key));
                    }
                    while(travelIterator.hasNext()){
                        String key = (String) travelIterator.next();
                        mapTravel.put(key, country_to_travel.getString(key));
                        TravelName.add(mapTravel.get(key));
                    }
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                CountrySpinner.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, CountryName));
                                TravelSpinner.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, TravelName));
                            }
                        });
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private JSONObject updateMedicalCenter(){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(getOptionURL).build();
        JSONObject alert_medical_center = null;
        try {
            Response response = client.newCall(request).execute();
            String responseData = response.body().string();
            try {
                json = new JSONObject(responseData);
                options = json.getJSONObject("options");
                alert_medical_center = options.getJSONObject("alert_medical_center");
                Log.d("alert_medical_center: ", ""+alert_medical_center);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return alert_medical_center;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void loadMedicalCenterSpinner(final String city){
        centerUpdateLabel.setText(spannableMessage);
        centerUpdateLabel.setTextColor(getResources().getColor(R.color.colorLightText));
        Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {
                MedicalCenterName.clear();
                mapMedicalCenter.clear();
                MedicalCenterName.add(0, "-Select medical center-");
                if (!city.equals("-Select your city-")){
                    citySelectionCounter += 1;
                    if (!postRequestDone){
                        Toast.makeText(getActivity(), city, Toast.LENGTH_SHORT).show();
                    }
                    try {
                        JSONObject alert_medical_center;
                        boolean flag = false;
                        for (String key : mapCity.keySet()) {
                            if (mapCity.get(key).equals(city)) {
                                GiantCityKey = key; //city code through selection
                            }
                        }
                        if(!postRequestDone){
                            alert_medical_center = (json.getJSONObject("options")).getJSONObject("alert_medical_center");
                            Log.d("Function checking: ", postRequestDone+" False execute "+alert_medical_center);
                        }
                        else {
                            alert_medical_center = updateMedicalCenter();
                            Log.d("Function checking: ", postRequestDone+" True execute"+alert_medical_center);
                        }
                        boolean indicator = false; //it is indicating whether the country is available in json for which medical centers to be updated
                        Iterator alertIterator = alert_medical_center.keys();
                        while (alertIterator.hasNext()){
                            String key = (String) alertIterator.next();
                            Log.d("alert_medical_keys: ", ""+GiantCountryKey+" "+key);
                            if (GiantCountryKey.equals(key)){
                                indicator = true;
                                Log.d("Indicator become: ", ""+indicator);
                            }
                        }
                        if (indicator) {
                            Log.d("If condition: ", "executed "+indicator);
                            JSONObject AllCityCode = alert_medical_center.getJSONObject(GiantCountryKey);
                            Iterator cityCodeIterator = AllCityCode.keys();
                            String cityKey = null;
                            while (cityCodeIterator.hasNext()) {
                                cityKey = (String) cityCodeIterator.next(); // cityKey e.g., 91 for Gujranwala
                                if (GiantCityKey.equals(cityKey)) {
                                    centerUpdateLabel.setVisibility(View.INVISIBLE);
                                    Log.d("City Found: ", "cityKey: "+cityKey+" GiantCity: "+GiantCityKey+" Name: " + city);
                                    flag = true;
                                    JSONObject AllCenters = AllCityCode.getJSONObject(cityKey);
                                    Iterator AllCentersIterator = AllCenters.keys();
                                    while (AllCentersIterator.hasNext()) {
                                        String centerCode = (String) AllCentersIterator.next();
                                        mapMedicalCenter.put(centerCode, AllCenters.getString(centerCode));
                                        MedicalCenterName.add(mapMedicalCenter.get(centerCode));
                                    }
                                }
                                else {
                                    centerUpdateLabel.setVisibility(View.VISIBLE);
                                    Log.d("Else: ","executed");
                                }
                                if (flag){
                                    break;
                                }
                            }
                            if (!flag) {
                                Log.d("City Not Found: ", "cityKey: "+cityKey+" GiantCity: "+GiantCityKey + " Name: " + city);
                            }
                        }
                        else{
                            centerUpdateLabel.setVisibility(View.VISIBLE);
                            Log.d("Indicator stay: ",""+indicator);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                AlertAdapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()), android.R.layout.simple_spinner_dropdown_item, MedicalCenterName);
                AlertAdapter.notifyDataSetChanged(); //It is informing to the adapter that the data-set has changed after each function call
                MedicalCenterSpinner.setAdapter(AlertAdapter);
            }
        });

    }

    private void medicalCenterPostRequest(String updateCenterURL){
        postRequestDone = true;
        RequestBody requestBody = new FormBody.Builder()
                .add("country", GiantCountryKey)
                .add("city", GiantCityKey)
                .add("country_to_travel", GiantTravelKey).build();
        Request request = new Request.Builder().url(updateCenterURL).post(requestBody).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
            }
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String MedicalResponse = response.body().string();
                Log.d("Response: ", MedicalResponse);
                if (MedicalResponse.equals("true")) {
                    loadMedicalCenterSpinner(CityToast);
                    ProgressLoader.dismiss();
                    medicalSelectionCounter = 0;
                    Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (MedicalCenterName.size() > 1) {
                                medicalErrorLabel.setTextColor(getResources().getColor(R.color.colorGreen));
                                medicalErrorLabel.setVisibility(View.VISIBLE);
                                Toast.makeText(getActivity(), "Medical Centers Updated", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else {
                    ProgressLoader.dismiss();
                    centerUpdateLabel.setText("No medical centers are available for above selection.");
                    centerUpdateLabel.setTextColor(getResources().getColor(R.color.colorRed));
                    medicalErrorLabel.setTextColor(getResources().getColor(R.color.colorRed));
                    medicalErrorLabel.setVisibility(View.INVISIBLE);
                    Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "No Medical Centers Found", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                Log.d("My Tag: ", "Function Called");
            }
        });

    }

    private void basicSettingPostRequest(String settingUpdateURL){
        Log.d("Giants",""+GiantCountryKey+" "+GiantCityKey+" "+GiantTravelKey+" "+GiantMedicalKey);

        RequestBody requestBody = new FormBody.Builder()
                .add("your_country", GiantCountryKey)
                .add("your_city", GiantCityKey)
                .add("country_to_travel", GiantTravelKey)
                .add("alert_medical_center", GiantMedicalKey).build();
        Request request = new Request.Builder().url(settingUpdateURL).post(requestBody).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d("Medical", "Fail");
            }
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Log.d("Response: ", Objects.requireNonNull(response.body()).string());
                ProgressLoader.dismiss();
                Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "Settings updated", Toast.LENGTH_SHORT).show();
                    }
                });
                Log.d("My Tag: ", "Function Called");
            }
        });

    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.basicUpdateButton:
                if(CountryToast.equals("-Select your country-")){
                    countryErrorLabel.setVisibility(View.VISIBLE);
                }
                if(TravelToast.equals("-Select your GCC country-")){
                    travelErrorLabel.setVisibility(View.VISIBLE);
                }

                if(CityToast.equals("-Select your city-")){
                    cityErrorLabel.setVisibility(View.VISIBLE);
                }

                if(MedicalToast.equals("-Select medical center-")){
                    medicalErrorLabel.setVisibility(View.VISIBLE);
                }
                if(!CountryToast.equals("-Select your country-") && !CityToast.equals("-Select your city-") && !TravelToast.equals("-Select your GCC country-") && !MedicalToast.equals("-Select medical center-")){
                    fetchData.progressLoader(ProgressLoader);
                    basicSettingPostRequest(settingUpdateURL);
                    Log.d("Medical", "Called");
                }
                break;
        }
    }



    private void clickableText(){
        String updateMessage = getString(R.string.medical_message);
        spannableMessage = new SpannableString(updateMessage);
        ClickableSpan clickHere = new ClickableSpan() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(@NonNull View widget) {
                Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(CountryToast.equals("-Select your country-")){
                            countryErrorLabel.setVisibility(View.VISIBLE);
                        }
                        if(TravelToast.equals("-Select your GCC country-")){
                            travelErrorLabel.setVisibility(View.VISIBLE);
                        }

                        if(CityToast.equals("-Select your city-")){
                            cityErrorLabel.setVisibility(View.VISIBLE);
                        }
                        if(!CountryToast.equals("-Select your country-") && !CityToast.equals("-Select your city-") && !TravelToast.equals("-Select your GCC country-")){
                            fetchData.progressLoader(ProgressLoader);
                            medicalCenterPostRequest(updateCenterURL);
                        }
                    }
                });
            }
        };
        spannableMessage.setSpan(clickHere, 32, 42, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        centerUpdateLabel.setText(spannableMessage);
        centerUpdateLabel.setMovementMethod(LinkMovementMethod.getInstance());

    }



}
