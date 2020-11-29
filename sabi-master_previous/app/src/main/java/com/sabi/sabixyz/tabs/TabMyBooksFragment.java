package com.sabi.sabixyz.tabs;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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


public class TabMyBooksFragment extends Fragment {
    private RecyclerView recyclerView_mybooks;
    //private RecyclerView.Adapter recycleradapter_mybooks;
    private RecyclerAdapter recycleradapter_mybooks;
    private SharedPreferences userInfoPreference;
    private SharedPreferences.Editor editor;
    private List<ListItem> listitems;
    private EditText searchKeyWord;
    public static LinearLayout ll_search;
    FragmentManager fragmentManage;

    public TabMyBooksFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userInfoPreference = getActivity().getSharedPreferences(getString(R.string.user_sharePreference_Key), MODE_PRIVATE);
        editor = userInfoPreference.edit();
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
        searchKeyWord = view.findViewById(R.id.edt_search_keyword);
        ll_search = view.findViewById(R.id.ll_search);

        searchKeyWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length()==1){
                    recycleradapter_mybooks.getFilter().filter("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()>2){
                    recycleradapter_mybooks.getFilter().filter(searchKeyWord.getText().toString());
                }

            }
        });

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
                String myBook_list =userInfoPreference.getString(getString(R.string.mybook_list), "");
                Log.e("mybooklist", myBook_list);

                try {
                    JSONArray array = new JSONArray(myBook_list);
                    Log.e("response", array.toString());

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject o = array.getJSONObject(i);
                        ListItem item = new ListItem(o.getString("book_title"),  o.getString("book_desc"),o.getString("book_content"),o.getString("book_author"),o.getString("book_cover"), o.getString("book_id"), o.getString("book_amount"),o.getString("book_path"));
                        listitems.add(item);
                    }

                    getActivity().runOnUiThread(new Thread() {
                        @Override
                        public void run() {
                            recycleradapter_mybooks = new RecyclerAdapter(listitems, getContext(), 2);
                            recyclerView_mybooks.setAdapter(recycleradapter_mybooks);
                            recycleradapter_mybooks.notifyDataSetChanged();
                        }
                    });


                } catch (JSONException ex) {
                    ex.printStackTrace();
                }

                //listitems = gson.toJson()
                //listitems=gson.toJson(myBook_list);
               // List<ListItem> appList = new ArrayList<String>(Arrays.<ListItem>asList(myBook_list));
                //listitems = appList;

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
                            ListItem item = new ListItem(o.getString("book_title"),  o.getString("book_desc"),o.getString("book_content"),o.getString("book_author"),o.getString("book_cover"), o.getString("book_id"), o.getString("book_amount"),o.getString("book_path"));
                            listitems.add(item);
                        }
                        editor.putString(getString(R.string.mybook_list), data);
                        editor.apply();
                        editor.commit();
                        getActivity().runOnUiThread(new Thread() {
                            @Override
                            public void run() {
                                recycleradapter_mybooks = new RecyclerAdapter(listitems, getContext(), 2);
                                recyclerView_mybooks.setAdapter(recycleradapter_mybooks);
                                recycleradapter_mybooks.notifyDataSetChanged();
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
