package com.example.sabixyz;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.example.sabixyz.helper.Requests;
import com.example.sabixyz.model.ListItem;
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
 private RecyclerView recyclerView;
 private RecyclerView.Adapter adapter;
 private List<ListItem> listitems;

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
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        //recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        // Set layout manager.
        recyclerView.setLayoutManager(gridLayoutManager);

        listitems = new ArrayList<>();
/*
        for(int i = 0; i<=20; i++){
            ListItem listItem = new ListItem("Heading"+ (i+1), "Lorem Ipsum Dollor dummy text","");
            listitems.add(listItem);
        }
        adapter = new RecyclerAdapter(listitems, getContext());
        recyclerView.setAdapter(adapter);
*/
        loadRecyclerViewData();
        return view;
    }

    private void loadRecyclerViewData() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading Data");
        progressDialog.show();

        Requests.fetchRecyclerViewData(new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try {
                    JSONArray array = new JSONArray(response);
                    for (int i=0; i<array.length(); i++){
                        JSONObject o = array.getJSONObject(i);
                        ListItem item = new ListItem(o.getString("name"), o.getString("realname"),o.getString("imageurl"));
                        listitems.add(item);
                    }
                    adapter = new RecyclerAdapter(listitems, getContext());
                    recyclerView.setAdapter(adapter);

                    Log.e("Msg", array.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
               },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();

                }
            },getContext());
    }


}
