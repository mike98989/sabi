package com.example.sabixyz.tabs;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.sabixyz.R;
import com.example.sabixyz.adapter.RecyclerAdapter;
import com.example.sabixyz.helper.Requests;
import com.example.sabixyz.model.ListItem;

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
import static java.security.AccessController.getContext;


public class TabMyBooksFragment extends Fragment {
    private RecyclerView recyclerView_mybooks;
    private RecyclerView.Adapter recycleradapter_mybooks;
    private SharedPreferences userInfoPreference;
    private List<ListItem> listitems;
    FragmentManager fragmentManage;

    public TabMyBooksFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userInfoPreference = getActivity().getSharedPreferences(getString(R.string.user_sharePreference_Key), MODE_PRIVATE);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_tab_my_books, container, false);
        recyclerView_mybooks = view.findViewById(R.id.myBooksRecyclerView);
        recyclerView_mybooks.setHasFixedSize(true);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        // Set layout manager.
        recyclerView_mybooks.setLayoutManager(gridLayoutManager);

        listitems = new ArrayList<>();
        this.loadRecyclerViewData();
        return view;
    }



    private void loadRecyclerViewData() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading Data");
        progressDialog.show();

        Map<String, Object> map = new HashMap<>();
        map.put("DeviceToken", userInfoPreference.getString(getString(R.string.user_token), ""));

        Requests.fetchRecyclerViewDataNew(map, "my_books", new Callback() {
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
                            ListItem item = new ListItem(o.getString("book_title"),  o.getString("book_desc"),o.getString("book_content"),o.getString("book_author"),o.getString("book_cover"), o.getString("book_id"), o.getString("book_amount"));
                            listitems.add(item);
                        }

                        getActivity().runOnUiThread(new Thread() {
                            @Override
                            public void run() {
                                recycleradapter_mybooks = new RecyclerAdapter(listitems, getContext(), 2);
                                recyclerView_mybooks.setAdapter(recycleradapter_mybooks);
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
