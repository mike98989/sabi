package com.example.sabixyz.tabs;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
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


public class index2Fragment extends Fragment {
    private RecyclerView recyclerView2;
    private RecyclerView.Adapter recycleradapter2;
    private List<ListItem> listitems;
    SwipeRefreshLayout pullToRefresh2;
    FragmentManager fragmentManager;
    public index2Fragment() {
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
        View view =  inflater.inflate(R.layout.fragment_index2, container, false);
        pullToRefresh2 = view.findViewById(R.id.pullToRefresh2);
        recyclerView2 = view.findViewById(R.id.recyclerView2);
        recyclerView2.setHasFixedSize(true);
        //recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        // Set layout manager.
        recyclerView2.setLayoutManager(gridLayoutManager);

        listitems = new ArrayList<>();

        this.loadRecyclerViewData();

        pullToRefresh2.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            int Refreshcounter = 1;
            @Override
            public void onRefresh() {

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                if (Build.VERSION.SDK_INT >= 26) {
                    ft.setReorderingAllowed(false);
                }
                ft.detach(index2Fragment.this).attach(index2Fragment.this).commit();


                if (Refreshcounter > 5) {
                    Toast.makeText(getContext(), "No more data to load!!",
                            Toast.LENGTH_SHORT).show();
                }

                Refreshcounter = Refreshcounter + 1;
                recycleradapter2.notifyDataSetChanged();
                pullToRefresh2.setRefreshing(false);

            }
        });


        return view;
    }


    private void loadRecyclerViewData() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading Data");
        progressDialog.show();
        Requests.fetchRecyclerViewData("best_sellers", new com.android.volley.Response.Listener<String>() {
                                           @Override
                                           public void onResponse(String response) {
                                               progressDialog.dismiss();

                                               if(!response.isEmpty()) {
                                                   try {
                                                       JSONArray array = new JSONArray(response);
                                                       //Log.e("response", array.toString());

                                                       for (int i = 0; i < array.length(); i++) {
                                                           JSONObject o = array.getJSONObject(i);
                                                           ListItem item = new ListItem(o.getString("book_title"),  o.getString("book_desc"),o.getString("book_author"),o.getString("book_cover"), o.getString("book_id"),o.getString("book_amount"));
                                                           listitems.add(item);
                                                       }
                                                       recycleradapter2 = new RecyclerAdapter(listitems, getContext());
                                                       recyclerView2.setAdapter(recycleradapter2);

                                                       //Log.e("Msg", array.toString());

                                                   } catch (JSONException e) {
                                                       e.printStackTrace();
                                                   }
                                               }else{
                                                   //Log.e("result", response.toString());
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
