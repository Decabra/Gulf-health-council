package com.example.ghc;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class MoreFragment extends Fragment {

    private ListView listView;
    private ArrayList<String> arrayList;
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
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listView.setAdapter(new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, arrayList));
            }
        });


    return rootView;
    }
}
