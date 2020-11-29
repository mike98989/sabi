package com.sabi.sabixyz.tabs;

import android.app.ProgressDialog;

import android.content.SharedPreferences;
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
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.sabi.sabixyz.MainActivity;
import com.sabi.sabixyz.MyBooksFragment;
import com.sabi.sabixyz.R;
import com.sabi.sabixyz.adapter.RecyclerAdapter;
import com.sabi.sabixyz.helper.Requests;
import com.sabi.sabixyz.model.ListItem;

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

import static android.content.Context.MODE_PRIVATE;
import static com.sabi.sabixyz.MainActivity.tv_header_title;


public class index1Fragment extends Fragment {
    private RecyclerView recyclerView;
    //private RecyclerView.Adapter recycleradapter;
    private RecyclerAdapter recyclerAdapter;
    private List<ListItem> listitems;
    SwipeRefreshLayout pullToRefresh;
    String api_to_call, query_ID="";
    LinearLayout LInearLayout_NoContentYet;
    private EditText searchKeyWord;
    private SharedPreferences userInfoPreference;
    public static LinearLayout ll_search;
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
        userInfoPreference = getActivity().getSharedPreferences(getString(R.string.user_sharePreference_Key), MODE_PRIVATE);
       View view =  inflater.inflate(R.layout.fragment_index1, container, false);
       LInearLayout_NoContentYet = view.findViewById(R.id.linearlayout_no_content);
        pullToRefresh = view.findViewById(R.id.pullToRefresh);
        recyclerView = view.findViewById(R.id.recyclerView);
        searchKeyWord = view.findViewById(R.id.edt_search_keyword);
        ll_search = view.findViewById(R.id.ll_search);

        searchKeyWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length()==1){
                    recyclerAdapter.getFilter().filter("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()>2){
                    recyclerAdapter.getFilter().filter(searchKeyWord.getText().toString());
                }

            }
        });

        recyclerView.setHasFixedSize(true);


        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        // Set layout manager.
        recyclerView.setLayoutManager(gridLayoutManager);

        if (getArguments() != null) {
            api_to_call = getArguments().getString("api_to_call");
            query_ID = getArguments().getString("ID");

            /////////////MAKE MAIN ACTIVITY SEARCH BOTTON CLICK ABLE
            MainActivity.mSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (ll_search.getVisibility() == View.VISIBLE) {
                        ll_search.setVisibility(View.GONE);
                    } else {
                        ll_search.setVisibility(View.VISIBLE);
                    }
                }
            });


        }else{
            api_to_call = "get_all_books";
        }

        listitems = new ArrayList<>();
        //recyclerAdapter = new RecyclerAdapter(listitems, getContext(), 1);
        //recyclerView.setAdapter(recyclerAdapter);
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
                recyclerAdapter.notifyDataSetChanged();
                pullToRefresh.setRefreshing(false);

            }
        });

       return view;
    }




    @Override
    public void onResume() {
        super.onResume();
        Log.e("onresume","Got here resumed index1frag");
    }

    private void loadRecyclerViewData() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading Data");
        progressDialog.show();

        Map<String, Object> map = new HashMap<>();
        map.put("category_ID", query_ID);
        map.put("user_id", userInfoPreference.getString(getString(R.string.user_id), ""));

        Requests.fetchRecyclerViewDataNew(map, api_to_call, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                progressDialog.dismiss();
                //Log.e("response", "Somoething went wrong! please try again");
                getActivity().runOnUiThread(new Thread() {
                    @Override
                    public void run() {
                     Toast.makeText(getContext(), "Something is wrong with your internet connection", Toast.LENGTH_LONG).show();

                tv_header_title.setText(R.string.menu_my_books);
                Fragment myFragment = new MyBooksFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, myFragment).addToBackStack(null).commit();
                    }
                });
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                progressDialog.dismiss();
                String res = Objects.requireNonNull(response.body()).string();
                Log.e("res1", res);

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
                                ListItem item = new ListItem(o.getString("book_title"), o.getString("book_desc"), o.getString("book_content"), o.getString("book_author"), o.getString("book_cover"), o.getString("book_id"), o.getString("book_amount"),o.getString("book_path"));
                                listitems.add(item);
                            }

                            getActivity().runOnUiThread(new Thread() {
                                @Override
                                public void run() {
                                    recyclerAdapter = new RecyclerAdapter(listitems, getContext(), 1);
                                    recyclerView.setAdapter(recyclerAdapter);

                                    recyclerAdapter.notifyDataSetChanged();
                                    //recyclerAdapter.getFilter().filter("pu");
                                }
                            });
                        }else{
                            getActivity().runOnUiThread(new Thread() {@Override public void run() {
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
