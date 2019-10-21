package com.example.sabixyz.tabs;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.sabixyz.R;
import com.example.sabixyz.adapter.RecyclerAdapter;
import com.example.sabixyz.helper.Requests;
import com.example.sabixyz.model.ListItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TabSavedBooksFragment extends Fragment {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter recycleradapter;
    private List<ListItem> listitems;
    FragmentManager fragmentManage;


    public TabSavedBooksFragment() {
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
        View view = inflater.inflate(R.layout.fragment_tab_saved_books, container, false);
        recyclerView = view.findViewById(R.id.savedBooksRecyclerView);
        recyclerView.setHasFixedSize(true);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        // Set layout manager.
        recyclerView.setLayoutManager(gridLayoutManager);

        listitems = new ArrayList<>();
        this.loadRecyclerViewData();
        return view;
    }


    private void loadRecyclerViewData() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading Data");
        progressDialog.show();

        Requests.fetchRecyclerViewData("get_all_books", new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Log.e("response", response.toString());

                        try {
                            JSONArray array = new JSONArray(response);
                            //Log.e("response", array.toString());

                            for (int i=0; i<array.length(); i++){
                                JSONObject o = array.getJSONObject(i);
                                ListItem item = new ListItem(o.getString("book_title"),  o.getString("book_desc"),o.getString("book_content"),o.getString("book_author"),o.getString("book_cover"), o.getString("book_id"), o.getString("book_amount"));
                                listitems.add(item);
                            }
                            recycleradapter = new RecyclerAdapter(listitems, getContext(), 1);
                            recyclerView.setAdapter(recycleradapter);
                            //Log.e("Msg", array.toString());
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
