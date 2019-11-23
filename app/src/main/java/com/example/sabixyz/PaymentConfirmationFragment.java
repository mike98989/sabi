package com.example.sabixyz;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


public class PaymentConfirmationFragment extends Fragment implements View.OnClickListener {
private TextView tv_success_message;
private String paymentReference;
private Button gotoMyBooks;
    public PaymentConfirmationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.paymentReference = getArguments().getString("reference");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_payment_confirmation, container, false);
        tv_success_message = view.findViewById(R.id.tv_return_success_msg);
        gotoMyBooks = view.findViewById(R.id.btn_goto_my_books);
        gotoMyBooks.setOnClickListener(this);
        tv_success_message.setText("You payment reference is "+paymentReference);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_goto_my_books:
                MainActivity.tv_header_title.setText(R.string.menu_my_books);
                Fragment mybooks = new MyBooksFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, mybooks);
                fragmentTransaction.commit();
                break;
        }
    }
}
