package com.example.sabixyz.tabs;

import android.app.ProgressDialog;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.sabixyz.R;
import com.example.sabixyz.adapter.RecyclerAdapter;
import com.example.sabixyz.helper.Requests;
import com.example.sabixyz.listener.RecyclerItemClickListener;
import com.example.sabixyz.model.ListItem;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;


public class index1Fragment extends Fragment {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter recycleradapter;
    private List<ListItem> listitems;
    SwipeRefreshLayout pullToRefresh;
    String api_to_call, query_ID="";
    LinearLayout LInearLayout_NoContentYet;
    public index1Fragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view =  inflater.inflate(R.layout.fragment_index1, container, false);
       LInearLayout_NoContentYet = view.findViewById(R.id.linearlayout_no_content);
        pullToRefresh = view.findViewById(R.id.pullToRefresh);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);


        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        // Set layout manager.
        recyclerView.setLayoutManager(gridLayoutManager);

        if (getArguments() != null) {
            api_to_call = getArguments().getString("api_to_call");
            query_ID = getArguments().getString("ID");
        }else{
            api_to_call = "get_all_books";
        }

        listitems = new ArrayList<>();

        this.loadRecyclerViewData();

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            int Refreshcounter = 1;
            @Override
            public void onRefresh() {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                if (Build.VERSION.SDK_INT >= 26) {
                    ft.setReorderingAllowed(false);
                }
                ft.detach(index1Fragment.this).attach(index1Fragment.this).commit();


                if (Refreshcounter > 5) {
                    Toast.makeText(getContext(), "No more data to load!!",
                            Toast.LENGTH_SHORT).show();
                }

                Refreshcounter = Refreshcounter + 1;
                recycleradapter.notifyDataSetChanged();
                pullToRefresh.setRefreshing(false);

            }
        });


       return view;
    }


    private void loadRecyclerViewData() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading Data");
        progressDialog.show();

        Map<String, Object> map = new HashMap<>();
        map.put("category_ID", query_ID);

        Requests.fetchRecyclerViewDataNew(map, api_to_call, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                progressDialog.dismiss();
                Log.e("response", "Somoething went wrong! please try again");
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                progressDialog.dismiss();
                String res = Objects.requireNonNull(response.body()).string();
                Log.e("res", res);

                try {
                    JSONObject object = new JSONObject(res);
                    final String message = object.optString("message");
                    String status = object.optString("status");
                    String data = object.optString("data");
                    if(status.equals("1")) {
                        JSONArray array = new JSONArray(data);
                        Log.e("response", array.toString());

                        if(array.length()!=0) {
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject o = array.getJSONObject(i);
                                ListItem item = new ListItem(o.getString("book_title"), o.getString("book_desc"), o.getString("book_content"), o.getString("book_author"), o.getString("book_cover"), o.getString("book_id"), o.getString("book_amount"),"");
                                listitems.add(item);
                            }

                            getActivity().runOnUiThread(new Thread() {
                                @Override
                                public void run() {
                                    recycleradapter = new RecyclerAdapter(listitems, getContext(), 1);
                                    recyclerView.setAdapter(recycleradapter);

                                }
                            });
                        }else{
                            getActivity().runOnUiThread(new Thread() {
                                                            @Override
                                                            public void run() {
                            LInearLayout_NoContentYet.setVisibility(View.VISIBLE);
                                                            }
                            });
                        }
                    }
                    //Log.e("Msg", array.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, getContext());

    }

}
