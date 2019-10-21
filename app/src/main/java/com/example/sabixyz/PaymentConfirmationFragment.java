package com.example.sabixyz;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class PaymentConfirmationFragment extends Fragment {
private TextView tv_success_message;
private String paymentReference;
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
        tv_success_message.setText("You payment reference is "+paymentReference);
        return view;
    }

}
