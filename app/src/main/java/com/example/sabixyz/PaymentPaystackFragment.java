package com.example.sabixyz;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class PaymentPaystackFragment extends Fragment {
private TextView mAmountToPay;
private String strAmountToPay;

    public PaymentPaystackFragment() {
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
        View view =  inflater.inflate(R.layout.fragment_payment_paystack, container, false);
        if (getArguments() != null) {
            strAmountToPay = getArguments().getString("amount");
        }
        mAmountToPay = view.findViewById(R.id.tv_amount_to_pay);
        mAmountToPay.setText(getString(R.string.currency)+strAmountToPay);
        return view;
    }

}
