package com.example.ghc;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
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

public class AlertFragment extends Fragment implements View.OnClickListener{

    public EditText emailTextField;
    public EditText phoneTextField;
    public Spinner sendMedicalSpinner;
    public Spinner checkMedicalSpinner;
    private Switch debugSwitch;
    private Switch medicalSwitch;
    private TextView sendMedicalErrorLabel;
    private TextView checkMedicalErrorLabel;
    private Button alertUpdateButton;

    private ArrayList EmailSeparatorArray;
    private ArrayList PhoneSeparatorArray;
    private ArrayList sendMedicalArray;
    private ArrayList checkMedicalArray;

    private String getOptionURL;
    private String settingUpdateURL;
    private HashMap<String, String> mapSendMedical;

    private FetchData fetchData;

    private String sendMedicalToast;
    private String checkMedicalToast;

    private int sendMedicalCounter;
    private int checkMedicalCounter;
    private int InvalidNumberCounter;
    private int InvalidEmailCounter;

    private String GiantSendMedicalKey;
    private int GiantCheckMedicalKey;
    private String GiantPhoneTextKey;
    private String GiantEmailTextKey = "";

    private OkHttpClient client;

    private ProgressDialog ProgressLoader;

    private NetworkConsistency networkConsistency;
    private AlertDialog alertDialog;

    private boolean isLoadAlertSpinnerCalled = false;
    private boolean isButtonClicked = false;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.alert_section_layout, container, false);
        fetchData = new FetchData();
        fetchData.setupUI(rootView.findViewById(R.id.alertSurface), getActivity());
        networkConsistency = new NetworkConsistency(getContext());
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialog = fetchData.AlertDialogMessage(alertDialogBuilder);
        client = new OkHttpClient();

        EmailSeparatorArray = new ArrayList();
        PhoneSeparatorArray = new ArrayList();
        sendMedicalArray = new ArrayList();
        checkMedicalArray = new ArrayList();

        mapSendMedical = new HashMap<>();

        emailTextField = rootView.findViewById(R.id.emailTextField);
        phoneTextField = rootView.findViewById(R.id.phoneTextField);
        sendMedicalSpinner = rootView.findViewById(R.id.sendMedicalSpinner);
        checkMedicalSpinner = rootView.findViewById(R.id.checkMedicalSpinner);
        debugSwitch = rootView.findViewById(R.id.debugSwitch);
        medicalSwitch = rootView.findViewById(R.id.medicalSwitch);
        alertUpdateButton = rootView.findViewById(R.id.alertUpdateButton);

        alertUpdateButton.setOnClickListener(this);

        getOptionURL = "https://work.appizia.com/lb/api/get-options-data";
        settingUpdateURL = "https://work.appizia.com/lb/api/update-settings";

        sendMedicalCounter = 0;
        checkMedicalCounter = 0;
        InvalidNumberCounter = 0;
        InvalidEmailCounter = 0;

        sendMedicalErrorLabel = rootView.findViewById(R.id.sendMedicalErrorLabel);
        checkMedicalErrorLabel = rootView.findViewById(R.id.checkMedicalErrorLabel);
        checkMedicalErrorLabel.setVisibility(View.INVISIBLE);
        sendMedicalErrorLabel.setVisibility(View.INVISIBLE);

        ProgressLoader = new ProgressDialog(getActivity());

        isLoadAlertSpinnerCalled = true;
        AsyncTaskRunner runner = new AsyncTaskRunner();
        runner.execute();

        sendMedicalArray.add(0, "-Send medical list after every-");
        checkMedicalArray.add(0, "-Check center after every-");

        sendMedicalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sendMedicalToast = sendMedicalSpinner.getItemAtPosition(sendMedicalSpinner.getSelectedItemPosition()).toString();
                if(!sendMedicalToast.equals("-Send medical list after every-")){
                    sendMedicalErrorLabel.setVisibility(View.INVISIBLE);
                    sendMedicalCounter++;
                    for (String key: mapSendMedical.keySet()){
                        if (mapSendMedical.get(key).equals(sendMedicalToast)){
                            GiantSendMedicalKey = key;
                        }
                    }
                    Toast.makeText(getActivity(), sendMedicalToast, Toast.LENGTH_SHORT).show();

                }
                else if(sendMedicalToast.equals("-Send medical list after every-") && sendMedicalCounter > 0){
                    sendMedicalErrorLabel.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        checkMedicalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                checkMedicalToast = checkMedicalSpinner.getItemAtPosition(checkMedicalSpinner.getSelectedItemPosition()).toString();
                if(!checkMedicalToast.equals("-Check center after every-")){
                    checkMedicalErrorLabel.setVisibility(View.INVISIBLE);
                    checkMedicalCounter++;
                    for (int i = 0; i < checkMedicalArray.size(); i++){
                        if (checkMedicalArray.get(i).equals(checkMedicalToast)){
                            GiantCheckMedicalKey = i;

                            Log.d("Tag",checkMedicalToast+" "+GiantCheckMedicalKey);
                        }
                    }
                    Toast.makeText(getActivity(), checkMedicalToast, Toast.LENGTH_SHORT).show();

                }
                else if(checkMedicalToast.equals("-Check center after every-") && checkMedicalCounter > 0){
                    checkMedicalErrorLabel.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return rootView;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void loadAlertSpinner() throws JSONException {

        try {
            JSONObject options = fetchData.getOptions(getOptionURL, client);
            JSONObject send_medical_list_after_minutes = options.getJSONObject("send_medical_list_after_minutes");
            JSONArray check_after_time = options.getJSONArray("check_after_time");
            Iterator sendMedicalIterator = send_medical_list_after_minutes.keys();
            while( sendMedicalIterator.hasNext() ){
                String sendMedicalKey = (String) sendMedicalIterator.next();
                mapSendMedical.put(sendMedicalKey, send_medical_list_after_minutes.getString(sendMedicalKey));
                sendMedicalArray.add(mapSendMedical.get(sendMedicalKey));
            }
            for (int i = 0; i < check_after_time.length(); i++){
                checkMedicalArray.add(check_after_time.getString(i));
            }
            Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable(){
                @Override
                public void run() {
                    sendMedicalSpinner.setAdapter(new ArrayAdapter<String>(Objects.requireNonNull(getActivity()), android.R.layout.simple_selectable_list_item, sendMedicalArray));
                    checkMedicalSpinner.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_selectable_list_item, checkMedicalArray));
                }
            });
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<String> commaInputSeparator(ArrayList<String> array, String input){
        int arrayIndex = 0;
        StringBuilder buffer = new StringBuilder();
        String data = null;
        char[] s = input.toCharArray();
        for (int i = 0; i < s.length; i++){
            if (s[i] != ',') {
                buffer.append(s[i]);
//                Log.d("Tag: ", "If Data: "+buffer);
            }
            else if ( s[i] == ','){
                data = buffer.toString();
                buffer = new StringBuilder();
            //    Log.d("Tag: ", "Else_If Data: "+data);
                array.add(arrayIndex, data);
             //   Log.d("Tag: ", "Else_If Array Data: "+array.get(arrayIndex));
                arrayIndex++;
            }
            if ( i == (s.length - 1)) {
                data = buffer.toString();
            //    Log.d("Tag: ", "Else Data: " + data);
                array.add(arrayIndex, data);
            //    Log.d("Tag: ", "Else Array Data: " + array.get(arrayIndex));
                arrayIndex++;
            }
        }
        return array;
    }

    private void phoneInspectionMethod(EditText editText) throws NumberParseException {
        InvalidNumberCounter = 0;
        String phonePattern = "^[+][0-9]{8,15}$";   //PK E165 format
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        Phonenumber.PhoneNumber phoneChecker;
        boolean isValid, isPossibleNumber, isValidNumberForRegion;
        String phoneNumbers;
        PhoneSeparatorArray.clear();
        GiantPhoneTextKey = editText.getText().toString().trim();
        ArrayList phoneArray = commaInputSeparator(PhoneSeparatorArray, GiantPhoneTextKey);
        for (Object indexPhone: phoneArray){
          //  Log.d("indexPhone: ", ""+indexPhone);
            phoneNumbers = (String) indexPhone;
            if (phoneNumbers.matches(phonePattern) && phoneNumbers.startsWith("+92")) {
         //       Log.d("PatternMatch: ", "true");
                phoneChecker = phoneNumberUtil.parse(phoneNumbers, "PK");
                isPossibleNumber = phoneNumberUtil.isPossibleNumber(phoneChecker);
                isValidNumberForRegion = phoneNumberUtil.isValidNumberForRegion(phoneChecker, "PK");
                isValid = phoneNumberUtil.isValidNumber(phoneChecker);
            //    Log.d("Phone Valid: ", ""+isValid);
            //    Log.d("isPossibleNumber: ", ""+isPossibleNumber);
            //    Log.d("isValidForRegion: ", ""+isValidNumberForRegion);
                if (!isValid || !isPossibleNumber || !isValidNumberForRegion){
                    InvalidNumberCounter = 1;
                    break;
                }
            }
            else{
             //   Log.d("PatternMatch: ", "false");
                InvalidNumberCounter = 1;
                break;
            }
        //    Log.d("InvalidNumberCounter: ", ""+InvalidNumberCounter);
        }
    }

    private void emailInspectionMethod(EditText editText){
        GiantEmailTextKey = editText.getText().toString().trim();
        EmailSeparatorArray.clear();
        InvalidEmailCounter = 0;
       // Log.d("Invalid Email ", " "+InvalidEmailCounter);
        ArrayList<String>  emailArray = commaInputSeparator(EmailSeparatorArray, GiantEmailTextKey);
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        for (String indexEmail: emailArray){
        //    Log.d("indexEmail: ", indexEmail+"");
            if (!indexEmail.matches(emailPattern)){
          //      Log.d("Inside If: ", "Condition");
                InvalidEmailCounter = 1;
                break;
            }
        }
        //Log.d("InvalidEmailCounter: ", ""+InvalidEmailCounter);
    }

    private void alertSettingPostRequest(String settingUpdateURL){
        RequestBody requestBody = new FormBody.Builder()
                .add("email_to", GiantEmailTextKey)
                .add("sms_to", GiantPhoneTextKey)
                .add("send_medical_list_after_minutes", GiantSendMedicalKey)
                .add("check_after_time", ""+GiantCheckMedicalKey).build();
        Request request = new Request.Builder().url(settingUpdateURL).post(requestBody).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Log.d("Alert: ", Objects.requireNonNull(response.body()).string());
                ProgressLoader.dismiss();
                toastMessage("Settings updated");
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.alertUpdateButton:
                if(networkConsistency.networkStatus()) {
                    emailInspectionMethod(emailTextField);
                    try {
                        phoneInspectionMethod(phoneTextField);
                    } catch (NumberParseException e) {
                        e.printStackTrace();
                    }
                    if (InvalidEmailCounter > 0) {
                        emailTextField.setError("Invalid input");
                    }
                    if (InvalidNumberCounter > 0 || GiantPhoneTextKey.equals("")) {
                        phoneTextField.setError("Empty field or invalid input");
                    }
                    if (sendMedicalToast.equals("-Send medical list after every-")) {
                        sendMedicalErrorLabel.setVisibility(View.VISIBLE);
                    }
                    if (checkMedicalToast.equals("-Check center after every-")) {
                        checkMedicalErrorLabel.setVisibility(View.VISIBLE);
                    }
                    if (InvalidEmailCounter == 0 && !GiantPhoneTextKey.equals("") && InvalidNumberCounter == 0 && !sendMedicalToast.equals("-Send medical list after every-") && !checkMedicalToast.equals("-Check center after every-")) {
                        fetchData.progressLoader(ProgressLoader);
                        isButtonClicked = true;
                        AsyncTaskRunner runner = new AsyncTaskRunner();
                        runner.execute();
                    }
                }
                else{
                    if (!alertDialog.isShowing()){
                        alertDialog.show();
                    }
                }
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void toastMessage(final String message){
        Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), message , Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class AsyncTaskRunner extends AsyncTask{

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected Object doInBackground(Object[] objects) {
            if (isLoadAlertSpinnerCalled){
                try {
                    loadAlertSpinner();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (isButtonClicked){
                alertSettingPostRequest(settingUpdateURL);
            }
            isLoadAlertSpinnerCalled = false;isButtonClicked = false;
            return null;
        }
    }
}