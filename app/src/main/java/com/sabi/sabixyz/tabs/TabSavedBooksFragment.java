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

public class TabSavedBooksFragment extends Fragment {
    private RecyclerView recyclerView_my_saved_books;
    //private RecyclerView.Adapter recycleradapter_my_saved_books;
    private RecyclerAdapter recycleradapter_my_saved_books;
    private SharedPreferences userInfoPreference;
    private List<ListItem> listitems;
    private EditText searchKeyWord;
    public static LinearLayout ll_search;
    LinearLayout LInearLayout_NoContentYet;
    FragmentManager fragmentManage;


    public TabSavedBooksFragment() {
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
        View view = inflater.inflate(R.layout.fragment_tab_saved_books, container, false);
        recyclerView_my_saved_books = view.findViewById(R.id.savedBooksRecyclerView);
        recyclerView_my_saved_books.setHasFixedSize(true);
        //LInearLayout_NoContentYet = view.findViewById(R.id.linearlayout_no_content);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView_my_saved_books.setLayoutManager(gridLayoutManager);
        searchKeyWord = view.findViewById(R.id.edt_search_keyword);
        ll_search = view.findViewById(R.id.ll_search);

        searchKeyWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length()==1){
                    recycleradapter_my_saved_books.getFilter().filter("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()>2){
                    recycleradapter_my_saved_books.getFilter().filter(searchKeyWord.getText().toString());
                }

            }
        });

        listitems = new ArrayList<>();
        this.loadRecyclerViewData();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("onresume","Got here resumed");
    }

    private void loadRecyclerViewData() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading Data");
        progressDialog.show();

        Map<String, Object> map = new HashMap<>();
        map.put("DeviceToken", userInfoPreference.getString(getString(R.string.user_token), ""));

        Requests.fetchRecyclerViewDataNew(map, "my_saved_books", new Callback() {
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
                        //if(array.length()!=0) {
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject o = array.getJSONObject(i);
                                ListItem item = new ListItem(o.getString("book_title"),  o.getString("book_desc"),o.getString("book_content"),o.getString("book_author"),o.getString("book_cover"), o.getString("book_id"), o.getString("book_amount"),o.getString("book_path"));
                                listitems.add(item);
                            }

                            getActivity().runOnUiThread(new Thread() {
                                @Override
                                public void run() {
                                    recycleradapter_my_saved_books = new RecyclerAdapter(listitems, getContext(), 1);
                                    recyclerView_my_saved_books.setAdapter(recycleradapter_my_saved_books);
                                    recycleradapter_my_saved_books.notifyDataSetChanged();
                                }
                            });
/*                        }
                        else{
                                getActivity().runOnUiThread(new Thread() {@Override public void run() {
                                    Toast.makeText(getContext(), "No Content Yet!", Toast.LENGTH_LONG).show();
                                    //LInearLayout_NoContentYet.setVisibility(View.VISIBLE);
                                }
                                });
                            }*/
                    }
                    //Log.e("Msg", array.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, getContext());
    }

}
