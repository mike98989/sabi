package com.sabi.sabixyz;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.sabi.sabixyz.adapter.CategoriesRecyclerAdapter;
import com.sabi.sabixyz.helper.Requests;
import com.sabi.sabixyz.model.CategoriesList;

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


public class CategoriesScreenFragment extends Fragment {
    private RecyclerView recyclerView_categories;
    //private RecyclerView.Adapter recycleradapter_categories;
    private CategoriesRecyclerAdapter recycleradapter_categories;
    private List<CategoriesList> listitems;
    public static LinearLayout ll_search;
    private EditText searchKeyWord;
    public CategoriesScreenFragment() {
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
        View view= inflater.inflate(R.layout.fragment_categories_screen, container, false);
        MainActivity.tv_header_title.setText(R.string.menu_categories);
        recyclerView_categories = view.findViewById(R.id.myCategoriesView);
        recyclerView_categories.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        // Set layout manager.
        recyclerView_categories.setLayoutManager(gridLayoutManager);
        ll_search = view.findViewById(R.id.ll_search);
        searchKeyWord = view.findViewById(R.id.edt_search_keyword);
        listitems = new ArrayList<>();
        this.loadRecyclerViewData();


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

        searchKeyWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length()==1){
                    recycleradapter_categories.getFilter().filter("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()>2){
                    recycleradapter_categories.getFilter().filter(searchKeyWord.getText().toString());
                }

            }
        });


        return view;
    }

    private void loadRecyclerViewData() {

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading Data");
        progressDialog.show();

        Map<String, Object> map = new HashMap<>();
        map.put("", "");

        Requests.fetchRecyclerViewDataNew(map, "get_categories", new Callback() {
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
                            for (int i = 0; i < array.length(); i++) {
                            JSONObject o = array.getJSONObject(i);
                            CategoriesList item = new CategoriesList(o.getString("category_id"),  o.getString("category_title"),o.getString("category_desc"),o.getString("category_short_desc"),o.getString("image_url"));
                            listitems.add(item);
                        }

                        getActivity().runOnUiThread(new Thread() {
                            @Override
                            public void run() {
                                recycleradapter_categories = new CategoriesRecyclerAdapter(listitems, getContext(), 1);
                                recyclerView_categories.setAdapter(recycleradapter_categories);
                            }
                        });
                    }
                    //Log.e("Msg", array.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, getContext());

    }

}
