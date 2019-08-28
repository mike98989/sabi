package com.example.sabixyz;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.sabixyz.adapter.RecyclerAdapter;
import com.example.sabixyz.adapter.TabAdapter;
import com.example.sabixyz.helper.Requests;
import com.example.sabixyz.model.ListItem;
import com.example.sabixyz.tabs.index1Fragment;
import com.example.sabixyz.tabs.index2Fragment;
import com.example.sabixyz.tabs.index3Fragment;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;


public class LandingScreenFragment extends Fragment {

    private TabAdapter tabadapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    public LandingScreenFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_landing_screen, container, false);

        viewPager = view.findViewById(R.id.viewPager);
        tabLayout = view.findViewById(R.id.tabLayout);
        tabadapter = new TabAdapter(this.getFragmentManager());
        tabadapter.addFragment(new index1Fragment(), "All Books");
        tabadapter.addFragment(new index2Fragment(), "Best Sellers");
        tabadapter.addFragment(new index3Fragment(), "Most View");
        viewPager.setAdapter(tabadapter);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }



}
