package com.example.sabixyz;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import static android.content.Context.MODE_PRIVATE;

public class BookDetailsFragment extends Fragment implements View.OnClickListener {
String Title, Description, imageUrl, itemId;
private TextView mTitle, mDescription;
private ImageView mImageView;
private Button mBuyBookButton, mSavebutton;
private SharedPreferences userInfoPreference;

    public BookDetailsFragment() {
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
        userInfoPreference = getActivity().getSharedPreferences(getString(R.string.user_sharePreference_Key), MODE_PRIVATE);
        View view =  inflater.inflate(R.layout.fragment_book_details, container, false);
        mTitle = view.findViewById(R.id.tv_book_title);
        mDescription = view.findViewById(R.id.tv_book_desc);
        mImageView = view.findViewById(R.id.imgview_book_cover);
        mBuyBookButton = view.findViewById(R.id.btn_buy_book);
        mBuyBookButton.setOnClickListener(this);

        if (getArguments() != null) {
            Title = getArguments().getString("title");
            mTitle.setText(Title);
            //Log.e("desc", getArguments().getString("description"));
            mDescription.setText(getArguments().getString("description").replaceAll("\\n","\n"));
            Picasso.get().load("http://172.20.10.3/sabi/"+getArguments().getString("imageurl")).into(mImageView);
            mBuyBookButton.setText("Buy Book "+getString(R.string.currency)+getArguments().getString("amount"));
            //Log.e("Title", Title);
        }
        return view;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_buy_book:

                Bundle b = new Bundle();
                b.putString("title", getArguments().getString("title"));
                b.putString("amount", getArguments().getString("amount"));
                b.putString("email", userInfoPreference.getString(getString(R.string.user_email), ""));

                Fragment myFragment = new PaymentPaystackFragment();
                myFragment.setArguments(b);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, myFragment).addToBackStack(null).commit();
                break;
        }
    }
}
