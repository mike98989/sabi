package com.example.sabixyz.tabs;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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


public class index3Fragment extends Fragment {
    private RecyclerView recyclerView3;
    private RecyclerView.Adapter recycleradapter3;
    private List<ListItem> listitems;
    SwipeRefreshLayout pullToRefresh3;
    FragmentManager fragmentManager;

    public index3Fragment() {
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
        View view =  inflater.inflate(R.layout.fragment_index3, container, false);
        pullToRefresh3 = view.findViewById(R.id.pullToRefresh);
        recyclerView3 = view.findViewById(R.id.recyclerView);
        recyclerView3.setHasFixedSize(true);
        //recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        // Set layout manager.
        recyclerView3.setLayoutManager(gridLayoutManager);

        listitems = new ArrayList<>();

        this.loadRecyclerViewData();

        pullToRefresh3.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            int Refreshcounter = 1;
            @Override
            public void onRefresh() {

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                if (Build.VERSION.SDK_INT >= 26) {
                    ft.setReorderingAllowed(false);
                }
                ft.detach(index3Fragment.this).attach(index3Fragment.this).commit();


                if (Refreshcounter > 5) {
                    Toast.makeText(getContext(), "No more data to load!!",
                            Toast.LENGTH_SHORT).show();
                }

                Refreshcounter = Refreshcounter + 1;
                recycleradapter3.notifyDataSetChanged();
                pullToRefresh3.setRefreshing(false);

            }
        });


        return view;
    }


    private void loadRecyclerViewData() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading Data");
        progressDialog.show();
        Requests.fetchRecyclerViewData("most_views", new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        if(!response.isEmpty()) {
                            try {
                                JSONArray array = new JSONArray(response);
                                //Log.e("response", array.toString());

                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject o = array.getJSONObject(i);
                                    ListItem item = new ListItem(o.getString("book_title"),  o.getString("book_desc"),o.getString("book_content"),o.getString("book_author"),o.getString("book_cover"), o.getString("book_id"), o.getString("book_amount"),"");
                                    listitems.add(item);
                                }
                                recycleradapter3 = new RecyclerAdapter(listitems, getContext(),1);
                                recyclerView3.setAdapter(recycleradapter3);

                                //Log.e("Msg", array.toString());

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else{
                            Log.e("result", response.toString());
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
