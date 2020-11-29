package com.sabi.sabixyz.tabs;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.sabi.sabixyz.R;
import com.sabi.sabixyz.adapter.RecyclerAdapter;
import com.sabi.sabixyz.helper.Requests;
import com.sabi.sabixyz.model.ListItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class index3Fragment extends Fragment {
    private RecyclerView recyclerView3;
    //private RecyclerView.Adapter recycleradapter3;
    private RecyclerAdapter recyclerAdapter3;
    private List<ListItem> listitems;
    SwipeRefreshLayout pullToRefresh3;
    public static LinearLayout ll_search;
    private EditText searchKeyWord;
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
        searchKeyWord = view.findViewById(R.id.edt_search_keyword);
        ll_search = view.findViewById(R.id.ll_search3);

        searchKeyWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length()==1){
                    recyclerAdapter3.getFilter().filter("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()>2){
                    recyclerAdapter3.getFilter().filter(searchKeyWord.getText().toString());
                }

            }
        });

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
                recyclerAdapter3.notifyDataSetChanged();
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
                                    ListItem item = new ListItem(o.getString("book_title"), o.getString("book_desc"), o.getString("book_content"), o.getString("book_author"), o.getString("book_cover"), o.getString("book_id"), o.getString("book_amount"),o.getString("book_path"));
                                    listitems.add(item);
                                }
                                recyclerAdapter3 = new RecyclerAdapter(listitems, getContext(),1);
                                recyclerView3.setAdapter(recyclerAdapter3);

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
