package com.example.ghc;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Objects;

public class MoreFragment extends Fragment {

    private ListView listView;
    private ArrayList<String> arrayList;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView  = inflater.inflate(R.layout.more_section_layout, container, false);

        listView = rootView.findViewById(R.id.listView_more_section);
        arrayList = new ArrayList<>();
        arrayList.add("About us");
        arrayList.add("Privacy policy");
        arrayList.add("Terms and conditions");
        arrayList.add("Contact us");
        Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {
                listView.setAdapter(new ArrayAdapter<>(Objects.requireNonNull(getActivity()), android.R.layout.simple_list_item_1, arrayList));
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        Intent intentAbout = new Intent(view.getContext(), AboutActivity.class);
                        startActivity(intentAbout);
                        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        break;
                    case 1:
                        Intent intentPrivacy = new Intent(view.getContext(), PrivacyActivity.class);
                        startActivity(intentPrivacy);
                        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        break;
                    case 2:
                        Intent intentTerms = new Intent(view.getContext(),TermsActivity.class);
                        startActivity(intentTerms);
                        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        break;
                    case 3:
                        Intent intentContact = new Intent(view.getContext(), ContactActivity.class);
                        startActivity(intentContact);
                        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        break;
                }
            }
        });

    return rootView;
    }
}
